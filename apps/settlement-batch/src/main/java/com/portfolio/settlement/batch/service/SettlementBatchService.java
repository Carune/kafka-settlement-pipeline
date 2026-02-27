package com.portfolio.settlement.batch.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.settlement.batch.domain.OutboxEvent;
import com.portfolio.settlement.batch.domain.SettlementLedger;
import com.portfolio.settlement.batch.repository.OutboxEventRepository;
import com.portfolio.settlement.batch.repository.SettlementLedgerRepository;
import com.portfolio.settlement.batch.repository.SettlementSourceTransactionRepository;

@Service
public class SettlementBatchService {

    private static final String ADJUSTMENT_TOPIC = "settlement.adjustment.v1";

    private final SettlementSourceTransactionRepository sourceTransactionRepository;
    private final SettlementLedgerRepository settlementLedgerRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public SettlementBatchService(
        SettlementSourceTransactionRepository sourceTransactionRepository,
        SettlementLedgerRepository settlementLedgerRepository,
        OutboxEventRepository outboxEventRepository,
        ObjectMapper objectMapper
    ) {
        this.sourceTransactionRepository = sourceTransactionRepository;
        this.settlementLedgerRepository = settlementLedgerRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SettlementRunResult run(LocalDate businessDate) {
        List<MerchantSettlementAggregate> targets = sourceTransactionRepository.aggregateByBusinessDate(businessDate);

        int savedLedgerCount = 0;
        int createdOutboxCount = 0;
        for (MerchantSettlementAggregate target : targets) {
            boolean exists = settlementLedgerRepository.existsByBusinessDateAndMerchantId(
                businessDate,
                target.merchantId()
            );
            if (exists) {
                continue;
            }

            BigDecimal differenceAmount = target.expectedAmount().subtract(target.actualAmount());
            SettlementLedger ledger = SettlementLedger.builder()
                .merchantId(target.merchantId())
                .businessDate(businessDate)
                .expectedAmount(target.expectedAmount())
                .actualAmount(target.actualAmount())
                .differenceAmount(differenceAmount)
                .status(differenceAmount.compareTo(BigDecimal.ZERO) == 0 ? "완료" : "보정필요")
                .createdAt(LocalDateTime.now())
                .build();
            settlementLedgerRepository.save(ledger);
            savedLedgerCount++;

            if (differenceAmount.compareTo(BigDecimal.ZERO) != 0) {
                outboxEventRepository.save(
                    OutboxEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .topic(ADJUSTMENT_TOPIC)
                        .eventKey(target.merchantId())
                        .payloadJson(toPayloadJson(businessDate, target, differenceAmount))
                        .status("대기")
                        .retryCount(0)
                        .createdAt(LocalDateTime.now())
                        .build()
                );
                createdOutboxCount++;
            }
        }

        return new SettlementRunResult(targets.size(), savedLedgerCount, createdOutboxCount);
    }

    private String toPayloadJson(LocalDate businessDate, MerchantSettlementAggregate target, BigDecimal differenceAmount) {
        AdjustmentEventPayload payload = new AdjustmentEventPayload(
            UUID.randomUUID().toString(),
            target.merchantId(),
            businessDate,
            target.expectedAmount(),
            target.actualAmount(),
            differenceAmount
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("보정 이벤트 직렬화에 실패했습니다.", e);
        }
    }
}
