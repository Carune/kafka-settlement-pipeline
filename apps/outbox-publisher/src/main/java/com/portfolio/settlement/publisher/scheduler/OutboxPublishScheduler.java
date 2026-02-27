package com.portfolio.settlement.publisher.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublishScheduler {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublishScheduler.class);

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:5000}")
    public void publishPendingEvents() {
        log.info("아웃박스 폴링 실행.");
    }
}
