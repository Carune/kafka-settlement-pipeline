package com.portfolio.settlement.batch.job;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.portfolio.settlement.batch.service.SettlementBatchService;
import com.portfolio.settlement.batch.service.SettlementRunResult;

@Configuration
public class SettlementJobConfig {
    private static final Logger log = LoggerFactory.getLogger(SettlementJobConfig.class);

    @Bean
    public Job settlementJob(JobRepository jobRepository, Step settlementStep) {
        return new JobBuilder("settlementJob", jobRepository)
            .start(settlementStep)
            .build();
    }

    @Bean
    public Step settlementStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet settlementTasklet
    ) {
        return new StepBuilder("settlementStep", jobRepository)
            .tasklet(settlementTasklet, transactionManager)
            .build();
    }

    @Bean
    public Tasklet settlementTasklet(SettlementBatchService settlementBatchService) {
        return (contribution, chunkContext) -> {
            Object businessDateParam = chunkContext.getStepContext().getJobParameters().get("businessDate");
            LocalDate businessDate = LocalDate.parse(String.valueOf(businessDateParam));
            SettlementRunResult result = settlementBatchService.run(businessDate);
            log.info(
                "정산 배치 완료. businessDate={}, 처리대상={}, 원장저장={}, 보정이벤트={}",
                businessDate,
                result.targetMerchantCount(),
                result.savedLedgerCount(),
                result.createdOutboxCount()
            );
            return RepeatStatus.FINISHED;
        };
    }
}
