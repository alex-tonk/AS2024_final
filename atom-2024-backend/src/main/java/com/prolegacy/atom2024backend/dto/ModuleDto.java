package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.ModuleId;
import lombok.Data;

@Data
public class ModuleDto {
    ModuleId id;
    ModuleDto module;
    String name;
    Long orderNumber;
}
