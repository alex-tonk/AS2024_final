package com.prolegacy.atom2024backend.cron;

import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.repositories.TestGroupRepository;
import com.prolegacy.atom2024backend.services.TestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@Log4j2
public class TestCron {

    @Autowired
    private TestGroupRepository testGroupRepository;

    @Autowired
    private TestService testService;

    @Scheduled(fixedRate = 10000)
    public void updateTasks() {
        try {
            var testGroups = testGroupRepository.findAllByEndDateIsNull();
            testGroups.forEach(group -> {
                var tests = Optional.ofNullable(group.getTests())
                        .orElseGet(ArrayList::new)
                        .stream()
                        .filter(t -> t.getTestStatus() != TestStatus.FINISHED
                                && t.getTestStatus() != TestStatus.ERROR
                                && t.getTestStatus() != TestStatus.CANCELLED)
                        .filter(t -> t.getStandEndpoint().getStand().getComputationType() != ComputationType.EMULATED)
                        .toList();
                tests.forEach(test -> {
                    try {
                        testService.updateTest(test);
                    } catch (Exception e) {
                        log.warn("Ошибка обновления данных испытания [id = %s]".formatted(test.getId()), e);
                    }
                });
            });
        } catch (Exception e) {
            log.warn("Ошибка обновления данных испытаний", e);
        }
    }
}
