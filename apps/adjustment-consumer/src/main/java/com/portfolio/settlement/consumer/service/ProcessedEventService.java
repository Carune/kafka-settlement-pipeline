package com.portfolio.settlement.consumer.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.settlement.consumer.domain.ProcessedEvent;
import com.portfolio.settlement.consumer.repository.ProcessedEventRepository;

@Service
public class ProcessedEventService {
    private static final Logger log = LoggerFactory.getLogger(ProcessedEventService.class);

    private final ProcessedEventRepository processedEventRepository;

    public ProcessedEventService(ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional(readOnly = true)
    public boolean isProcessed(String eventId) {
        validateEventId(eventId);
        return processedEventRepository.existsByEventId(eventId);
    }

    @Transactional
    public void markProcessed(String eventId, String eventKey, String sourceTopic) {
        validateEventId(eventId);
        ProcessedEvent processedEvent = ProcessedEvent.of(eventId, eventKey, sourceTopic, LocalDateTime.now());

        try {
            processedEventRepository.save(processedEvent);
        } catch (DataIntegrityViolationException e) {
            // Another consumer instance may have recorded the same event first.
            log.info("Duplicate processed-event record detected. eventId={}", eventId);
        }
    }

    private void validateEventId(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId must not be blank.");
        }
    }
}
