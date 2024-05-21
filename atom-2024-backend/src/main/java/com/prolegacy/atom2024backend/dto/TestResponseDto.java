package com.prolegacy.atom2024backend.dto;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TestResponseDto {
    private Long id;
    @JsonDeserialize(converter = NoUTCInstant.class)
    private Instant registrationDateTime;
    private JsonNode params;
    private ProductDto product;
    private PersonalDto registrator;
    private PersonalDto executor;
    @JsonDeserialize(converter = NoUTCInstant.class)
    private Instant executionStart;
    @JsonDeserialize(converter = NoUTCInstant.class)
    private Instant executionEnd;
    private JsonNode result;
    private TestStatus state;

    public static class NoUTCInstant implements Converter<LocalDateTime, Instant> {

        @Override
        public Instant convert(LocalDateTime localDateTime) {
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(LocalDateTime.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Instant.class);
        }
    }
}
