package com.portfolio.settlement.batch.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "settlement_ledger")
public class SettlementLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false, length = 64)
    private String merchantId;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "expected_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedAmount;

    @Column(name = "actual_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal actualAmount;

    @Column(name = "difference_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal differenceAmount;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static final class Builder {
        private final SettlementLedger target = new SettlementLedger();

        public Builder merchantId(String merchantId) {
            target.merchantId = merchantId;
            return this;
        }

        public Builder businessDate(LocalDate businessDate) {
            target.businessDate = businessDate;
            return this;
        }

        public Builder expectedAmount(BigDecimal expectedAmount) {
            target.expectedAmount = expectedAmount;
            return this;
        }

        public Builder actualAmount(BigDecimal actualAmount) {
            target.actualAmount = actualAmount;
            return this;
        }

        public Builder differenceAmount(BigDecimal differenceAmount) {
            target.differenceAmount = differenceAmount;
            return this;
        }

        public Builder status(String status) {
            target.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            target.createdAt = createdAt;
            return this;
        }

        public SettlementLedger build() {
            return target;
        }
    }
}
