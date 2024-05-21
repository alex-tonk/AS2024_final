package com.prolegacy.atom2024backend.common.parsing;

import com.prolegacy.atom2024backend.common.annotation.SimpleQueryClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@SimpleQueryClass
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private Map<String, MetadataField> fields;
}
