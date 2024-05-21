package com.prolegacy.atom2024integration.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.Instant;

@Data
public class ThermoDto {
    Long id;
    JsonNode params;
    JsonNode result;
    Instant registrationDateTime;
    Instant executionStart;
    Instant executionEnd;
    String state;
    PersonalDto registrator;
    PersonalDto executor;
    ProductDto product;
}
