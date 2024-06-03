package com.prolegacy.atom2024backend.common.parsing.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.parsing.FileParseResult;
import com.prolegacy.atom2024backend.common.parsing.Metadata;
import com.prolegacy.atom2024backend.common.parsing.MetadataField;
import com.prolegacy.atom2024backend.common.parsing.enums.MetadataNodeType;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CsvParser {
    private final Character delimiter;

    private CsvParser(Character delimiter) {
        this.delimiter = delimiter;
    }

    public static CsvParser withDelimiter(Character delimiter) {
        return new CsvParser(delimiter);
    }

    public List<FileParseResult> parse(InputStream inputStream) {
        OrderedMap<String, MetadataNodeType> columnTypes;
        List<ObjectNode> body = new ArrayList<>();

        try (Scanner scanner = new Scanner(inputStream)) {
            if (!scanner.hasNextLine()) {
                throw new BusinessLogicException("Первая строка должна содержать заголовки");
            }

            String headersRow = scanner.nextLine();

            columnTypes = Arrays.stream(headersRow.split(String.valueOf(delimiter)))
                    .collect(ListOrderedMap::new, (map, s) -> map.put(s, MetadataNodeType.NULL), ListOrderedMap::putAll);

            int rowNumber = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                body.add(parseRow(line, columnTypes, rowNumber));
                ++rowNumber;
            }
        }

        Metadata metadata = new Metadata(columnTypes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new MetadataField(null, e.getValue())))
        );
        return body.stream().map(value -> new FileParseResult(metadata, value)).toList();
    }

    private ObjectNode parseRow(String line, OrderedMap<String, MetadataNodeType> columnTypes, int rowNumber) {
        final ObjectNode bodyEntry = new ObjectNode(JsonNodeFactory.instance);

        String[] row = line.split(String.valueOf(delimiter));

        if (row.length > columnTypes.size()) {
            throw new BusinessLogicException("Число клеток в строке не может превышать число заголовков [Строка №%s]".formatted(rowNumber));
        }

        int cellIdx = 0;
        for (String header : columnTypes.keySet()) {
            Pair<MetadataNodeType, JsonNode> parsedCell = parseCell(
                    cellIdx < row.length ? row[cellIdx] : null,
                    columnTypes.get(header),
                    header,
                    cellIdx
            );

            columnTypes.put(header, parsedCell.getKey());
            bodyEntry.set(header, parsedCell.getValue());

            ++cellIdx;
        }

        return bodyEntry;
    }

    private static Pair<MetadataNodeType, JsonNode> parseCell(String cell, MetadataNodeType columnType, String header, int cellIdx) {
        MetadataNodeType cellType = MetadataNodeType.NULL;
        JsonNode value = NullNode.getInstance();

        if (!StringUtils.isBlank(cell)) {
            cell = cell.trim();
            switch (FileParseResult.findBestType(cell)) {
                case NUMBER -> {
                    value = new DecimalNode(BigDecimal.valueOf(Double.parseDouble(cell)));
                    cellType = MetadataNodeType.NUMBER;
                }
                case BOOLEAN -> {
                    value = BooleanNode.valueOf(Boolean.parseBoolean(cell));
                    cellType = MetadataNodeType.BOOLEAN;
                }
                default -> {
                    value = new TextNode(cell);
                    cellType = MetadataNodeType.STRING;

                }
            }
        }
        boolean columnTypeIsKnown = columnType != MetadataNodeType.NULL;
        if (!columnTypeIsKnown && cellType != MetadataNodeType.NULL) {
            columnType = cellType;
        } else if (columnTypeIsKnown && cellType != MetadataNodeType.NULL && cellType != columnType) {
            throw new BusinessLogicException("Гетерогенные данные в столбце [%s] клетка [%s]]".formatted(header, cellIdx));
        }

        return Pair.of(columnType, value);
    }
}
