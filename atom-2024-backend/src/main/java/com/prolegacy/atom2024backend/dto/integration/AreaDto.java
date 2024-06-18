package com.prolegacy.atom2024backend.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AreaDto {
    private BigDecimal x1;
    private BigDecimal y1;
    private BigDecimal x2;
    private BigDecimal y2;
}
