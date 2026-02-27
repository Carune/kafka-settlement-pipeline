package com.portfolio.settlement.batch.web;

import java.time.LocalDate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchTriggerController {

    private final JobLauncher jobLauncher;
    private final Job settlementJob;

    public BatchTriggerController(JobLauncher jobLauncher, Job settlementJob) {
        this.jobLauncher = jobLauncher;
        this.settlementJob = settlementJob;
    }

    @PostMapping("/settlement")
    public ResponseEntity<String> runSettlement(
        @RequestParam(name = "businessDate", required = false) String businessDate
    ) throws Exception {
        String targetDate = businessDate == null ? LocalDate.now().minusDays(1).toString() : businessDate;
        JobParameters params = new JobParametersBuilder()
            .addString("businessDate", targetDate)
            .addLong("requestedAt", System.currentTimeMillis())
            .toJobParameters();

        JobExecution execution = jobLauncher.run(settlementJob, params);
        return ResponseEntity.accepted()
            .body("정산 배치 실행 시작. executionId=%d, businessDate=%s".formatted(execution.getId(), targetDate));
    }
}
