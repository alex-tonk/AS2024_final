package com.prolegacy.atom2024backend.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttemptCheckResultDto {
    private AreaDto area;
    private List<String> features;
}
