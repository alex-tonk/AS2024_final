package com.prolegacy.atom2024backend.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.dto.PersonalDto;
import com.prolegacy.atom2024backend.dto.TestDto;
import com.prolegacy.atom2024backend.dto.TestResponseDto;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.entities.ids.TestId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Optional;

@Entity
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
public class Test {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TestId id;

    private Long outerId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode inData;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = true)
    private JsonNode outData;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus testStatus;
    private String registratorShortName;
    private String executorShortName;
    private Instant registrationDate;
    private Instant executionStartDate;
    private Instant executionEndDate;
    @ManyToOne
    @JoinColumn(name = "product_global_id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "stand_endpoint_id", nullable = false)
    private StandEndpoint standEndpoint;
    @ManyToOne
    @JoinColumn(name = "test_group_id", nullable = false)
    private TestGroup testGroup;

    public Test(StandEndpoint standEndpoint, TestGroup testGroup, Product product, TestDto dto) {
        this.inData = dto.getInData();
        this.testStatus = TestStatus.UNREGISTERED;
        this.testGroup = testGroup;
        this.standEndpoint = standEndpoint;
        this.product = product;
    }

    public void update(TestResponseDto result) {
        this.outerId = result.getId();
        this.testStatus = result.getState();
        this.registrationDate = result.getRegistrationDateTime();
        this.executionStartDate = result.getExecutionStart();
        this.executionEndDate = result.getExecutionEnd();
        if(result != null && !result.getResult().isEmpty()) {
            this.outData = result.getResult();
        }
        this.executorShortName = Optional.ofNullable(result.getExecutor())
                .map(PersonalDto::getCaption)
                .orElse(null);
        if(registratorShortName == null) {
            this.registratorShortName = Optional.ofNullable(result.getRegistrator())
                    .map(PersonalDto::getCaption)
                    .orElse(null);
        }
    }

    public void startEmulated() {
        this.testStatus = TestStatus.EXECUTING;
        this.registrationDate = Instant.now();
        this.executionStartDate = Instant.now();
        this.executorShortName = Optional.ofNullable(testGroup.getStartUser())
                .map(User::getShortName)
                .orElse(null);
    }

    public void finishEmulated(JsonNode data) {
        this.testStatus = TestStatus.FINISHED;
        this.executionEndDate = Instant.now();
        this.outData = data;
    }

    public void finishEmulatedWithError() {
        this.testStatus = TestStatus.ERROR;
        this.executionEndDate = Instant.now();
        this.outData = null;

    }

    public void cancel() {
        this.testStatus = TestStatus.CANCELLED;
    }
}
