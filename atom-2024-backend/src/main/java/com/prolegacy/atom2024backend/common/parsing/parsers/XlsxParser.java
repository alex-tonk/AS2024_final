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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


abstract public class XlsxParser {
    static public List<FileParseResult> parse(InputStream inputStream) {
        ArrayList<FileParseResult> res = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            for (Sheet sheet : workbook) {
                res.addAll(parseSheet(sheet));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static List<FileParseResult> parseSheet(FileInputStream fileInputStream, int sheetIndex) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
            return parseSheet(workbook.getSheetAt(sheetIndex));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<FileParseResult> parseSheet(FileInputStream fileInputStream, String sheetName) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
            return parseSheet(workbook.getSheet(sheetName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<FileParseResult> parseSheet(Sheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();

        Row headersRow = rows.next();
        if (headersRow == null) {
            throw new BusinessLogicException("Первая строка должна содержать заголовки");
        }

        OrderedMap<String, MetadataNodeType> columnTypes = StreamSupport.stream(headersRow.spliterator(), false).map(cell -> {
            if (!cell.getCellType().equals(CellType.STRING)) {
                throw new BusinessLogicException("Заголовки должны быть строками");
            }
            return cell.getStringCellValue();
        }).collect(ListOrderedMap::new, (map, s) -> map.put(s, MetadataNodeType.NULL), ListOrderedMap::putAll);

        ArrayList<ObjectNode> body = new ArrayList<>();
        while (rows.hasNext()) {
            body.add(parseRow(rows.next(), columnTypes));
        }

        Metadata metadata = new Metadata(columnTypes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new MetadataField(null, e.getValue())))
        );
        return body.stream().map(value -> new FileParseResult(metadata, value)).toList();
    }

    private static ObjectNode parseRow(Row row, OrderedMap<String, MetadataNodeType> columnTypes) {
        final ObjectNode bodyEntry = new ObjectNode(JsonNodeFactory.instance);

        if (row.getLastCellNum() > columnTypes.size()) {
            throw new BusinessLogicException("Число клеток в строке не может превышать число заголовков [Строка №%s]".formatted(row.getRowNum()));
        }

        int cellIdx = 0;
        for (String header : columnTypes.keySet()) {
            Pair<MetadataNodeType, JsonNode> parsedCell = parseCell(
                    row.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK),
                    columnTypes.get(header),
                    header
            );

            columnTypes.put(header, parsedCell.getKey());
            bodyEntry.set(header, parsedCell.getValue());

            ++cellIdx;
        }

        return bodyEntry;
    }

    private static Pair<MetadataNodeType, JsonNode> parseCell(Cell cell, MetadataNodeType columnType, String header) {
        MetadataNodeType cellType = MetadataNodeType.NULL;
        JsonNode value = NullNode.getInstance();

        switch (cell.getCellType()) {
            case NUMERIC -> {
                cellType = MetadataNodeType.NUMBER;
                value = new DecimalNode(BigDecimal.valueOf(cell.getNumericCellValue()));
            }
            case STRING -> {
                cellType = MetadataNodeType.STRING;
                value = new TextNode(cell.getStringCellValue());
            }
            case BOOLEAN -> {
                cellType = MetadataNodeType.BOOLEAN;
                value = BooleanNode.valueOf(cell.getBooleanCellValue());
            }
            case FORMULA ->
                    throw new BusinessLogicException("Формулы не поддерживаются [Клетка %s]".formatted(cell.getAddress()));
            case ERROR ->
                    throw new BusinessLogicException("Ошибки не поддерживаются [Клетка %s]".formatted(cell.getAddress()));
        }

        boolean columnTypeIsKnown = columnType != MetadataNodeType.NULL;
        if (!columnTypeIsKnown && cellType != MetadataNodeType.NULL) {
            columnType = cellType;
        } else if (columnTypeIsKnown && cellType != MetadataNodeType.NULL && cellType != columnType) {
            throw new BusinessLogicException("Гетерогенные данные в столбце [%s] клетка [%s]]".formatted(header, cell.getAddress()));
        }

        return Pair.of(columnType, value);
    }
}
