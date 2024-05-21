package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.ProductDto;
import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.dto.TestGroupDto;
import com.prolegacy.atom2024backend.entities.StandEndpoint;
import com.prolegacy.atom2024backend.entities.Test;
import com.prolegacy.atom2024backend.entities.TestGroup;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import com.prolegacy.atom2024backend.exceptions.ProductNotFoundException;
import com.prolegacy.atom2024backend.exceptions.StandEndpointNotFoundException;
import com.prolegacy.atom2024backend.exceptions.StandNotFoundException;
import com.prolegacy.atom2024backend.exceptions.TestGroupNotFoundException;
import com.prolegacy.atom2024backend.readers.TestGroupReader;
import com.prolegacy.atom2024backend.repositories.ProductRepository;
import com.prolegacy.atom2024backend.repositories.StandRepository;
import com.prolegacy.atom2024backend.repositories.TestGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


@Service
public class TestGroupService {
    @Autowired
    private TestGroupRepository testGroupRepository;

    @Autowired
    private TestGroupReader testGroupReader;

    @Autowired
    private StandRepository standRepository;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private TestService testService;

    @Autowired
    private ProductRepository productRepository;

    @Value("${auth.admin-role:#{null}}")
    private String adminRoleName;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TestGroupDto createTestGroup(TestGroupDto testGroupDto) {
        if (testGroupDto.getTests() == null || testGroupDto.getTests().isEmpty()) {
            throw new BusinessLogicException("Отсутствуют испытания");
        }
        var testGroup = new TestGroup(testGroupDto, userProvider.get());
        testGroupDto.getTests().forEach(testDto -> {
            var stand = Optional.ofNullable(testDto.getStandEndpoint())
                    .map(StandEndpointDto::getStand)
                    .map(StandDto::getId)
                    .flatMap(standRepository::findById)
                    .orElseThrow(StandNotFoundException::new);
            var standEndpoint = Optional.ofNullable(testDto.getStandEndpoint())
                    .map(StandEndpointDto::getId)
                    .map(stand::getEndpoint)
                    .orElseThrow(StandEndpointNotFoundException::new);
            var product = Optional.ofNullable(testDto.getProduct())
                    .map(ProductDto::getGlobalId)
                    .flatMap(productRepository::findById)
                    .orElseThrow(ProductNotFoundException::new);
            testGroup.addTest(
                    testDto,
                    standEndpoint,
                    product
            );
        });
        this.validateUser(testGroup);
        testGroupRepository.save(testGroup);
        startTests(testGroup);
        return testGroupReader.getTestGroup(testGroup.getId());
    }

    private void startTests(TestGroup testGroup) {
        try {
            testGroup.getTests().forEach(testService::startTest);
            testGroup.setStartDate(Instant.now());
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка запуска испытания");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TestGroupDto cancelTest(TestGroupId groupId) {
        var group = testGroupRepository.findById(groupId)
                .orElseThrow(TestGroupNotFoundException::new);
        this.validateUser(group);
        group.getTests()
                .stream()
                .filter(t -> t.getTestStatus() != TestStatus.FINISHED
                        && t.getTestStatus() != TestStatus.ERROR
                        && t.getTestStatus() != TestStatus.CANCELLED)
                .forEach(t -> {
                    switch (t.getStandEndpoint().getStand().getComputationType()) {
                        case REST -> t.cancel();
                        case EMULATED -> {
                            try {
                                Optional.ofNullable(testService.getEmulation(t.getId()))
                                        .ifPresent(f -> f.cancel(true));
                                t.cancel();
                            } catch (Exception ex) {
                                throw new BusinessLogicException("Не удалось остановить испытание");
                            }
                        }
                        default -> throw new BusinessLogicException("Неизвестный тип стенда");
                    }
                });
        return testGroupReader.getTestGroup(groupId);
    }

    public void validateUser(TestGroup testGroup) {
        var user = userProvider.get();
        String adminRoleWithPrefix = Optional.ofNullable(adminRoleName).map(adminRole -> "ROLE_" + adminRole)
                .orElseThrow(() -> new BusinessLogicException("Не найдена роль администратора"));
        if (user.getRoles()
                .stream()
                .filter(r -> adminRoleWithPrefix.equals(r.getName()))
                .findFirst()
                .isEmpty()) {
            if (testGroup.getTests().stream().map(Test::getStandEndpoint).map(StandEndpoint::getId).anyMatch(id -> !user.getAvailableEndpointsId().contains(id))) {
                throw new BusinessLogicException("Отсутствует право доступа к стенду");
            }
        }
    }
}
