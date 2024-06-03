package com.prolegacy.atom2024backend.common.parsing.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.common.parsing.FileParseResult;
import com.prolegacy.atom2024backend.common.parsing.Metadata;
import com.prolegacy.atom2024backend.common.parsing.MetadataField;
import com.prolegacy.atom2024backend.common.parsing.enums.MetadataNodeType;
import org.apache.commons.lang3.NotImplementedException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface ComplexParser {
    FileParseResult parse(InputStream inputStream);

    default Metadata parseMetadata(JsonNode body) {
        Map<String, MetadataField> res = new HashMap<>();
        for (Map.Entry<String, JsonNode> entry : body.properties()) {
            res.put(entry.getKey(), new MetadataField(null, getMetadataNodeType(entry.getValue())));
        }

        return new Metadata(res);
    }

    private static MetadataNodeType getMetadataNodeType(JsonNode node) {
        return switch (node.getNodeType()) {
            case NULL -> MetadataNodeType.NULL;
            case BOOLEAN -> MetadataNodeType.BOOLEAN;
            case NUMBER -> MetadataNodeType.NUMBER;
            case STRING -> MetadataNodeType.STRING;
            default -> throw new NotImplementedException();
        };
    }
}
