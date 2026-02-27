package com.portfolio.settlement.batch.service;

public record SettlementRunResult(
    int targetMerchantCount,
    int savedLedgerCount,
    int createdOutboxCount
) {
}
