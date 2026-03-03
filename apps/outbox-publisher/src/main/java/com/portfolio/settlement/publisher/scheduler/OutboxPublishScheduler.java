package com.portfolio.settlement.publisher.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.portfolio.settlement.publisher.service.OutboxPublishResult;
import com.portfolio.settlement.publisher.service.OutboxPublishService;

@Component
public class OutboxPublishScheduler {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublishScheduler.class);
    private final OutboxPublishService outboxPublishService;

    public OutboxPublishScheduler(OutboxPublishService outboxPublishService) {
        this.outboxPublishService = outboxPublishService;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:5000}")
    public void publishPendingEvents() {
        OutboxPublishResult result = outboxPublishService.publishPendingEvents();
        if (result.fetchedCount() == 0) {
            log.debug("아웃박스 폴링 실행. 발행 대상이 없습니다.");
            return;
        }

        log.info(
            "아웃박스 폴링 완료. 조회={}, 성공={}, 재시도예정={}, 최종실패={}",
            result.fetchedCount(),
            result.publishedCount(),
            result.retryScheduledCount(),
            result.failedCount()
        );
    }
}
