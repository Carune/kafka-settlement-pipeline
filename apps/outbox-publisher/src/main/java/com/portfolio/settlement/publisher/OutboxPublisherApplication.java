package com.portfolio.settlement.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OutboxPublisherApplication {
    public static void main(String[] args) {
        SpringApplication.run(OutboxPublisherApplication.class, args);
    }
}
