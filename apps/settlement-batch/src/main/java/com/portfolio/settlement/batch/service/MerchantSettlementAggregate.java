package com.portfolio.settlement.batch.service;

import java.math.BigDecimal;

public record MerchantSettlementAggregate(
    String merchantId,
    BigDecimal expectedAmount,
    BigDecimal actualAmount
) {
}
