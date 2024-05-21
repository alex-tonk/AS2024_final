package com.prolegacy.atom2024backend.common.parsing;

import com.prolegacy.atom2024backend.common.parsing.enums.MetadataNodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MetadataField {
    private String label;
    private MetadataNodeType nodeType;
}
