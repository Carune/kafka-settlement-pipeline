package com.portfolio.settlement.publisher.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.portfolio.settlement.publisher.domain.OutboxEvent;
import com.portfolio.settlement.publisher.repository.OutboxEventRepository;

@Service
public class OutboxPublishService {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublishService.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int batchSize;
    private final int maxRetryCount;
    private final long sendTimeoutMs;

    public OutboxPublishService(
        OutboxEventRepository outboxEventRepository,
        KafkaTemplate<String, String> kafkaTemplate,
        @Value("${app.outbox.batch-size:100}") int batchSize,
        @Value("${app.outbox.max-retry-count:5}") int maxRetryCount,
        @Value("${app.outbox.send-timeout-ms:3000}") long sendTimeoutMs
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.batchSize = batchSize;
        this.maxRetryCount = maxRetryCount;
        this.sendTimeoutMs = sendTimeoutMs;
    }

    public OutboxPublishResult publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
            OutboxEvent.STATUS_PENDING,
            PageRequest.of(0, batchSize)
        );

        int publishedCount = 0;
        int retryScheduledCount = 0;
        int failedCount = 0;

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayloadJson())
                    .get(sendTimeoutMs, TimeUnit.MILLISECONDS);
                event.markPublished(LocalDateTime.now());
                outboxEventRepository.save(event);
                publishedCount++;
            } catch (Exception e) {
                boolean retryable = event.markFailedAndCheckRetryable(maxRetryCount);
                outboxEventRepository.save(event);
                if (retryable) {
                    retryScheduledCount++;
                    log.warn(
                        "아웃박스 발행 실패. 재시도 예정. eventId={}, retryCount={}, topic={}",
                        event.getEventId(),
                        event.getRetryCount(),
                        event.getTopic(),
                        e
                    );
                } else {
                    failedCount++;
                    log.error(
                        "아웃박스 발행 최종 실패. eventId={}, retryCount={}, topic={}",
                        event.getEventId(),
                        event.getRetryCount(),
                        event.getTopic(),
                        e
                    );
                }
            }
        }

        return new OutboxPublishResult(
            pendingEvents.size(),
            publishedCount,
            retryScheduledCount,
            failedCount
        );
    }
}
