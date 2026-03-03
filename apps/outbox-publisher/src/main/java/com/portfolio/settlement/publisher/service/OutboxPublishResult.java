package com.portfolio.settlement.publisher.service;

public record OutboxPublishResult(
    int fetchedCount,
    int publishedCount,
    int retryScheduledCount,
    int failedCount
) {
}
