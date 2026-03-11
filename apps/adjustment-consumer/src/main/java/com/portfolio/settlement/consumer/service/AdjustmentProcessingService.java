package com.portfolio.settlement.consumer.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.settlement.consumer.service.dto.AdjustmentEventPayload;

@Service
public class AdjustmentProcessingService {
    private static final Logger log = LoggerFactory.getLogger(AdjustmentProcessingService.class);

    private final ObjectMapper objectMapper;

    public AdjustmentProcessingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void process(AdjustmentEventPayload payload, String key) {
        if (payload.differenceAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                "Negative difference is not auto-adjustable. merchantId=%s".formatted(payload.merchantId())
            );
        }

        log.info(
            "Adjustment processed. key={}, eventId={}, merchantId={}, businessDate={}, differenceAmount={}",
            key,
            payload.eventId(),
            payload.merchantId(),
            payload.businessDate(),
            payload.differenceAmount()
        );
    }

    public AdjustmentEventPayload parsePayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, AdjustmentEventPayload.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize adjustment event payload.", e);
        }
    }
}
