package com.portfolio.settlement.batch.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.settlement.batch.domain.SettlementSourceTransaction;
import com.portfolio.settlement.batch.service.MerchantSettlementAggregate;

public interface SettlementSourceTransactionRepository extends JpaRepository<SettlementSourceTransaction, Long> {

    @Query("""
        select new com.portfolio.settlement.batch.service.MerchantSettlementAggregate(
            t.merchantId,
            sum(t.expectedAmount),
            sum(t.actualAmount)
        )
        from SettlementSourceTransaction t
        where t.businessDate = :businessDate
        group by t.merchantId
        """)
    List<MerchantSettlementAggregate> aggregateByBusinessDate(@Param("businessDate") LocalDate businessDate);
}
