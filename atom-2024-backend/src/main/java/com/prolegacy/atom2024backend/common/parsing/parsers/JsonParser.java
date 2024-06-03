package com.prolegacy.atom2024backend.common.parsing.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolegacy.atom2024backend.common.parsing.FileParseResult;
import com.prolegacy.atom2024backend.common.parsing.Metadata;

import java.io.IOException;
import java.io.InputStream;

public class JsonParser implements ComplexParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static JsonParser INSTANCE = new JsonParser();

    private JsonParser() {
    }

    public FileParseResult parse(InputStream inputStream) {
        try {
            JsonNode body = objectMapper.readTree(inputStream);
            Metadata metadata = parseMetadata(body);

            return new FileParseResult(metadata, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
