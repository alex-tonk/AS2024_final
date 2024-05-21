package com.prolegacy.atom2024integration.common.parsing;

import com.prolegacy.atom2024integration.common.annotation.SimpleQueryClass;
import com.prolegacy.atom2024integration.common.parsing.enums.MetadataNodeType;
import lombok.Getter;

import java.util.Map;

@Getter
@SimpleQueryClass
public class Metadata {
    private final Map<String, MetadataNodeType> metadataObject;

    public Metadata(Map<String, MetadataNodeType> metadataObject) {
        this.metadataObject = metadataObject;
    }
}
