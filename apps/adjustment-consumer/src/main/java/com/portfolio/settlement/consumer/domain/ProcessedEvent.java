package com.portfolio.settlement.consumer.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_event")
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(name = "event_key", length = 128)
    private String eventKey;

    @Column(name = "source_topic", nullable = false, length = 128)
    private String sourceTopic;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public static ProcessedEvent of(String eventId, String eventKey, String sourceTopic, LocalDateTime processedAt) {
        ProcessedEvent event = new ProcessedEvent();
        event.eventId = eventId;
        event.eventKey = eventKey;
        event.sourceTopic = sourceTopic;
        event.processedAt = processedAt;
        return event;
    }
}
