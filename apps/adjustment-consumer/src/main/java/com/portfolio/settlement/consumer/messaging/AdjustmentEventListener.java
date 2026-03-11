package com.portfolio.settlement.consumer.messaging;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.portfolio.settlement.consumer.service.AdjustmentProcessingService;
import com.portfolio.settlement.consumer.service.AdjustmentRoutingService;
import com.portfolio.settlement.consumer.service.ProcessedEventService;
import com.portfolio.settlement.consumer.service.dto.AdjustmentEventPayload;

@Component
public class AdjustmentEventListener {
    private static final Logger log = LoggerFactory.getLogger(AdjustmentEventListener.class);
    private static final String RETRY_COUNT_HEADER = "x-retry-count";

    private final AdjustmentProcessingService adjustmentProcessingService;
    private final AdjustmentRoutingService adjustmentRoutingService;
    private final ProcessedEventService processedEventService;
    private final int maxRetryCount;
    private final String adjustmentTopic;
    private final String retryTopic;

    public AdjustmentEventListener(
        AdjustmentProcessingService adjustmentProcessingService,
        AdjustmentRoutingService adjustmentRoutingService,
        ProcessedEventService processedEventService,
        @Value("${app.kafka.max-retry-count:3}") int maxRetryCount,
        @Value("${app.kafka.adjustment-topic:settlement.adjustment.v1}") String adjustmentTopic,
        @Value("${app.kafka.retry-topic:settlement.adjustment.retry.5m.v1}") String retryTopic
    ) {
        this.adjustmentProcessingService = adjustmentProcessingService;
        this.adjustmentRoutingService = adjustmentRoutingService;
        this.processedEventService = processedEventService;
        this.maxRetryCount = maxRetryCount;
        this.adjustmentTopic = adjustmentTopic;
        this.retryTopic = retryTopic;
    }

    @KafkaListener(
        topics = "${app.kafka.adjustment-topic:settlement.adjustment.v1}",
        groupId = "${spring.kafka.consumer.group-id:adjustment-consumer-v1}"
    )
    public void onAdjustmentMessage(
        String payload,
        @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        processOrRoute(payload, key, 0, false);
    }

    @KafkaListener(
        topics = "${app.kafka.retry-topic:settlement.adjustment.retry.5m.v1}",
        groupId = "${spring.kafka.consumer.group-id:adjustment-consumer-v1}"
    )
    public void onRetryMessage(
        String payload,
        @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
        @Header(name = RETRY_COUNT_HEADER, required = false) byte[] retryCountHeader
    ) {
        int retryCount = parseRetryCount(retryCountHeader);
        processOrRoute(payload, key, retryCount, true);
    }

    private void processOrRoute(String payload, String key, int retryCount, boolean fromRetryTopic) {
        try {
            AdjustmentEventPayload eventPayload = adjustmentProcessingService.parsePayload(payload);
            String eventId = eventPayload.eventId();

            // Consumer idempotency: skip duplicated eventId instead of re-running business logic.
            if (processedEventService.isProcessed(eventId)) {
                log.info("Duplicate event skipped. eventId={}, key={}, retryCount={}", eventId, key, retryCount);
                return;
            }

            adjustmentProcessingService.process(eventPayload, key);
            processedEventService.markProcessed(eventId, key, fromRetryTopic ? retryTopic : adjustmentTopic);
            log.info("Adjustment handled. key={}, eventId={}, retryCount={}, fromRetryTopic={}", key, eventId, retryCount, fromRetryTopic);
        } catch (Exception e) {
            int nextRetryCount = retryCount + 1;
            if (nextRetryCount >= maxRetryCount) {
                adjustmentRoutingService.sendToDlq(payload, key, nextRetryCount, e.getMessage());
                log.error("Adjustment failed permanently. Sent to DLQ. key={}, retryCount={}", key, nextRetryCount, e);
                return;
            }

            adjustmentRoutingService.sendToRetry(payload, key, nextRetryCount);
            log.warn("Adjustment failed. Sent to retry topic. key={}, retryCount={}", key, nextRetryCount, e);
        }
    }

    private int parseRetryCount(byte[] retryCountHeader) {
        if (retryCountHeader == null || retryCountHeader.length == 0) {
            return 0;
        }
        String raw = new String(retryCountHeader, StandardCharsets.UTF_8);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse retry count header. fallback=0, raw={}", raw);
            return 0;
        }
    }
}
