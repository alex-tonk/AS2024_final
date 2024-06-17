package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.FeatureId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FeatureDto {
    private FeatureId id;

    private String code;
    private String name;
}
