package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.TraitId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TraitDto {
    private TraitId id;

    private String code;
    private String name;
    private String description;
}
