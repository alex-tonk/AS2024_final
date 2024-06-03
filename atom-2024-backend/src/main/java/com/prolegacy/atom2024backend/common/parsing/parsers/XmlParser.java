package com.prolegacy.atom2024backend.common.parsing.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.prolegacy.atom2024backend.common.parsing.FileParseResult;
import com.prolegacy.atom2024backend.common.parsing.Metadata;

import java.io.IOException;
import java.io.InputStream;

public class XmlParser implements ComplexParser {
    private static final XmlMapper xmlMapper = new XmlMapper();
    public static XmlParser INSTANCE = new XmlParser();

    private XmlParser() {
    }

    public FileParseResult parse(InputStream inputStream) {
        try {
            JsonNode body = xmlMapper.readTree(inputStream);
            Metadata metadata = parseMetadata(body);

            return new FileParseResult(metadata, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
