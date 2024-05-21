package com.prolegacy.atom2024backend.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.entities.ids.TestId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TestDto {
    private TestId id;
    private Long outerId;
    private JsonNode inData;
    private JsonNode outData;
    private TestStatus testStatus;
    private String executorShortName;
    private Instant registrationDate;
    private Instant executionStartDate;
    private Instant executionEndDate;
    private StandEndpointDto standEndpoint;
    private ProductDto product;

    private String productCaption;
    private String standEndpointDescription;
    private String testType;

    private Long executionSeconds;
}
