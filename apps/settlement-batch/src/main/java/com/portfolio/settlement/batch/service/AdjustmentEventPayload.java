package com.portfolio.settlement.batch.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdjustmentEventPayload(
    String eventId,
    String merchantId,
    LocalDate businessDate,
    BigDecimal expectedAmount,
    BigDecimal actualAmount,
    BigDecimal differenceAmount
) {
}
