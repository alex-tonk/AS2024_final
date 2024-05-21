package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StandDto {
    private StandId id;
    private String name;
    private String description;
    private String url;
    private ComputationType computationType;
}
