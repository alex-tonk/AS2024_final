package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.AttemptCheckResultId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttemptCheckResultDto {
    private AttemptCheckResultId id;

    private BigDecimal x1;
    private BigDecimal y1;
    private BigDecimal x2;
    private BigDecimal y2;

    private List<FeatureDto> features;

    private Boolean isAutomatic;
}