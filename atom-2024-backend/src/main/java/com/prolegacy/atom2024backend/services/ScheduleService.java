package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.entities.Attempt;
import com.prolegacy.atom2024backend.enums.AttemptStatus;
import com.prolegacy.atom2024backend.repositories.AttemptRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class ScheduleService {
    private final AttemptService attemptService;
    private final AttemptRepository attemptRepository;

    public ScheduleService(AttemptService attemptService, AttemptRepository attemptRepository) {
        this.attemptService = attemptService;
        this.attemptRepository = attemptRepository;
    }

    @Scheduled(fixedRate = 10L, timeUnit = TimeUnit.SECONDS)
    public void apiPinger() {
        List<Attempt> uncheckedAttempts = attemptRepository.getAllByAutoMarkIsNullAndAutoCheckFailedIsFalseAndStatusEquals(AttemptStatus.VALIDATION);
        for (Attempt uncheckedAttempt : uncheckedAttempts) {
            try {
                attemptService.checkAttempt(uncheckedAttempt);
            } catch (Exception e) {
                log.error("Ошибка автоматической проверки", e);
                attemptService.setAutoCheckFailed(uncheckedAttempt);
            }
        }
        attemptRepository.saveAll(uncheckedAttempts);
    }

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
    public void autoCompleteTasks() {
        Instant now = Instant.now();
        List<Attempt> inProgressAttempts = attemptRepository.getAllByStatusEquals(AttemptStatus.IN_PROGRESS);
        for (Attempt inProgressAttempt : inProgressAttempts) {
            if (inProgressAttempt.getStartDate().until(now, ChronoUnit.MINUTES) < inProgressAttempt.getTask().getTime())
                continue;

            attemptService.autoFailAttempt(inProgressAttempt);
        }
        attemptRepository.saveAll(inProgressAttempts);
    }
}
