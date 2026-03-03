package com.portfolio.settlement.publisher.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
    public static final String STATUS_PENDING = "대기";
    public static final String STATUS_PUBLISHED = "발행완료";
    public static final String STATUS_FAILED = "발행실패";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(name = "topic", nullable = false, length = 128)
    private String topic;

    @Column(name = "event_key", nullable = false, length = 128)
    private String eventKey;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTopic() {
        return topic;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public String getStatus() {
        return status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void markPublished(LocalDateTime publishedAt) {
        this.status = STATUS_PUBLISHED;
        this.publishedAt = publishedAt;
    }

    public boolean markFailedAndCheckRetryable(int maxRetryCount) {
        int nextRetryCount = retryCount == null ? 1 : retryCount + 1;
        this.retryCount = nextRetryCount;
        if (nextRetryCount >= maxRetryCount) {
            this.status = STATUS_FAILED;
            return false;
        }
        this.status = STATUS_PENDING;
        return true;
    }
}
