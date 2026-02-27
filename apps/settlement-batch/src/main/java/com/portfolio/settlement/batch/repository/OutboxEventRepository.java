package com.portfolio.settlement.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.settlement.batch.domain.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
}
