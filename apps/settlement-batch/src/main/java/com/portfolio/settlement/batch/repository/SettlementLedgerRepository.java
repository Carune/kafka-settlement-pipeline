package com.portfolio.settlement.batch.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.settlement.batch.domain.SettlementLedger;

public interface SettlementLedgerRepository extends JpaRepository<SettlementLedger, Long> {
    boolean existsByBusinessDateAndMerchantId(LocalDate businessDate, String merchantId);
}
