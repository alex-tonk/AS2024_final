package com.prolegacy.atom2024backend.common.parsing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.prolegacy.atom2024backend.common.parsing.enums.MetadataNodeType;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public record FileParseResult(Metadata metadata, JsonNode body) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String metadataJson() {
        return metadataJson(GsonFactory.defaultGson());
    }

    public String metadataJson(Gson gson) {
        return gson.toJson(metadata);
    }

    public String bodyJson() {
        return body.toString();
    }

    public Map<String, Object> bodyMap() {
        return objectMapper.convertValue(body, new TypeReference<>() {
        });
    }

    public static MetadataNodeType findBestType(String value) {
        if (NumberUtils.isCreatable(value)) {
            return MetadataNodeType.NUMBER;
        } else if (List.of("true", "false", "истина", "ложь").contains(value.toLowerCase(Locale.ROOT))) {
            return MetadataNodeType.BOOLEAN;
        } else {
            return MetadataNodeType.STRING;
        }
    }
}
