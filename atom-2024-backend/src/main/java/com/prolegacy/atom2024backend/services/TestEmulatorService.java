package com.prolegacy.atom2024backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.entities.Test;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.exceptions.TestGroupNotFoundException;
import com.prolegacy.atom2024backend.repositories.TestGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.function.Function;

@Service
public class TestEmulatorService {

    @Autowired
    private TestGroupRepository testGroupRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(maxAttempts = 10)
    public void finishEmulatedTest(Test t, JsonNode data) {
        var group = testGroupRepository.findById(t.getTestGroup().getId())
                .orElseThrow(TestGroupNotFoundException::new);
        var test = group.getTest(t.getId());
        test.finishEmulated(data);
        if (test.getTestGroup().getTests().stream().allMatch(tt -> tt.getTestStatus() == TestStatus.FINISHED)) {
            test.getTestGroup().setEndDate(
                    test.getTestGroup().getTests().stream()
                            .map(Test::getExecutionEndDate)
                            .max(Comparator.comparing(Function.identity(), Instant::compareTo))
                            .orElse(Instant.now())
            );
        }
        testGroupRepository.save(group);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(maxAttempts = 10)
    public void finishEmulatedTestWithError(Test t) {
        var group = testGroupRepository.findById(t.getTestGroup().getId())
                .orElseThrow(TestGroupNotFoundException::new);
        var test = group.getTest(t.getId());
        test.finishEmulatedWithError();
        testGroupRepository.save(group);
    }
}
