package com.portfolio.settlement.batch.domain;

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

    public static Builder builder() {
        return new Builder();
    }

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
        this.status = "발행완료";
        this.publishedAt = publishedAt;
    }

    public static final class Builder {
        private final OutboxEvent target = new OutboxEvent();

        public Builder eventId(String eventId) {
            target.eventId = eventId;
            return this;
        }

        public Builder topic(String topic) {
            target.topic = topic;
            return this;
        }

        public Builder eventKey(String eventKey) {
            target.eventKey = eventKey;
            return this;
        }

        public Builder payloadJson(String payloadJson) {
            target.payloadJson = payloadJson;
            return this;
        }

        public Builder status(String status) {
            target.status = status;
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            target.retryCount = retryCount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            target.createdAt = createdAt;
            return this;
        }

        public OutboxEvent build() {
            return target;
        }
    }
}
