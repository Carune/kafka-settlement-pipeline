package com.portfolio.settlement.consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.settlement.consumer.domain.ProcessedEvent;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByEventId(String eventId);
}
