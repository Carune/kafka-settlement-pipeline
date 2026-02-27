package com.portfolio.settlement.consumer.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentEventListener {
    private static final Logger log = LoggerFactory.getLogger(AdjustmentEventListener.class);

    @KafkaListener(
        topics = "${app.kafka.adjustment-topic:settlement.adjustment.v1}",
        groupId = "${spring.kafka.consumer.group-id:adjustment-consumer-v1}"
    )
    public void onMessage(
        String payload,
        @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("보정 이벤트 수신. key={}, payload={}", key, payload);
    }
}
