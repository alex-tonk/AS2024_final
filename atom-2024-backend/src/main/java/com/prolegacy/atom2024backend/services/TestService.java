package com.prolegacy.atom2024backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Strings;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.js.JsEvaluator;
import com.prolegacy.atom2024backend.common.js.JsEvaluatorFactory;
import com.prolegacy.atom2024backend.dto.PersonalDto;
import com.prolegacy.atom2024backend.dto.ProductDto;
import com.prolegacy.atom2024backend.dto.TestRequestDto;
import com.prolegacy.atom2024backend.dto.TestResponseDto;
import com.prolegacy.atom2024backend.entities.Test;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.entities.ids.TestId;
import com.prolegacy.atom2024backend.readers.TestGroupReader;
import com.prolegacy.atom2024backend.repositories.TestGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class TestService {
    private final RestTemplate restTemplate;
    private final TestGroupRepository testGroupRepository;
    private final TestGroupReader testGroupReader;
    private final JsEvaluatorFactory jsEvaluatorFactory;
    private final TestEmulatorService testEmulatorService;
    private final UserProvider userProvider;

    public final Cache<TestId, CompletableFuture<?>> emulations;

    public TestService(@Autowired RestTemplate restTemplate,
                       @Autowired TestGroupRepository testGroupRepository,
                       @Autowired TestGroupReader testGroupReader,
                       @Autowired JsEvaluatorFactory jsEvaluatorFactory,
                       @Autowired TestEmulatorService testEmulatorService,
                       @Autowired UserProvider userProvider,
                       @Value("${js-evaluator.timeout-seconds:10}") Long timeoutSeconds) {
        this.restTemplate = restTemplate;
        this.testGroupRepository = testGroupRepository;
        this.testGroupReader = testGroupReader;
        this.jsEvaluatorFactory = jsEvaluatorFactory;
        this.testEmulatorService = testEmulatorService;
        this.userProvider = userProvider;

        this.emulations = Caffeine.newBuilder()
                .expireAfterWrite(timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }

    public void startTest(Test test) {
        switch (test.getStandEndpoint().getStand().getComputationType()) {
            case REST -> startWithRestApi(test);
            case EMULATED -> startWithEmulation(test);
            default -> throw new BusinessLogicException("Неизвестный тип стенда");
        }
    }

    private void startWithRestApi(Test test) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(test.getStandEndpoint().getUrl())
                .build()
                .toUri();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        var result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(
                        TestRequestDto.builder()
                                .registrator(PersonalDto.EMPTY)
                                .product(
                                        ProductDto.builder()
                                                .id(test.getProduct().getId())
                                                .code(test.getProduct().getCode())
                                                .caption(test.getProduct().getCaption())
                                                .build()
                                ).params(test.getInData())
                                .build(),
                        headers
                ),
                TestResponseDto.class
        ).getBody();

        if (result == null) {
            throw new BusinessLogicException("Ошибка запуска испытания");
        }

        if (result.getState() == TestStatus.REGISTERED
                && (result.getRegistrator() == null || result.getRegistrator().getCaption() == null)) {
            result.setRegistrator(
                    PersonalDto.builder()
                            .caption(userProvider.get().getShortName())
                            .build()
            );
        }

        test.update(result);
    }

    private void startWithEmulation(Test test) {
        if (Strings.isNullOrEmpty(test.getStandEndpoint().getJsCode())) {
            throw new BusinessLogicException("Пустой JS-код");
        }
        try {
            var jsEvaluator = jsEvaluatorFactory.create();
            var product = ProductDto.builder()
                    .id(test.getProduct().getId())
                    .code(test.getProduct().getCode())
                    .caption(test.getProduct().getCaption())
                    .build();
            var future = jsEvaluator.evalJsonAsync(
                    """
                            var params = %s;
                            var product = %s;
                            function xxxDoomeg496x3xxx() {
                            %s
                            }
                                                        
                            xxxDoomeg496x3xxx();
                            """.formatted(
                                    test.getInData().toString(),
                                    JsEvaluator.getObjectMapper().valueToTree(product).toString(),
                                    test.getStandEndpoint().getJsCode()
                    )
            );
            future = future.handle((data, error) -> {
                        if (error == null && data != null) {
                            try {
                                this.testEmulatorService.finishEmulatedTest(test, data);
                            } catch (Exception e) {
                                this.testEmulatorService.finishEmulatedTestWithError(test);
                            }
                        } else {
                            this.testEmulatorService.finishEmulatedTestWithError(test);
                        }
                        return null;
                    }
            );
            emulations.put(test.getId(), future);
            test.startEmulated();
        } catch (ExecutionException e) {
            throw new BusinessLogicException("Ошибка запуска испытания");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateTest(Test test) {
        try {
            String httpString = test.getStandEndpoint().getUrl();
            httpString = httpString.substring(0, httpString.lastIndexOf('/') + 1) + test.getOuterId();
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(httpString)
                    .build()
                    .toUri();
            var result = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    TestResponseDto.class
            ).getBody();

            if (result == null) {
                throw new BusinessLogicException("Ошибка обновления данных испытания [id = %s]".formatted(test.getId()));
            }
            if (result.getState() == test.getTestStatus()) {
                return;
            }

            test.update(result);
            if (test.getTestGroup().getTests().stream().allMatch(t -> t.getTestStatus() == TestStatus.FINISHED)) {
                test.getTestGroup().setEndDate(
                        test.getTestGroup().getTests().stream()
                                .map(Test::getExecutionEndDate)
                                .max(Comparator.comparing(Function.identity(), Instant::compareTo))
                                .orElse(Instant.now())
                );
            }
            testGroupRepository.save(test.getTestGroup());
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка обновления данных испытания [id = %s]".formatted(test.getId()));
        }
    }

    public CompletableFuture<?> getEmulation(TestId testId) {
        return this.emulations.getIfPresent(testId);
    }
}
