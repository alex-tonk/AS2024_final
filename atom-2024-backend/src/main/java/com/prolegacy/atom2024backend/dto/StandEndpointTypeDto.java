package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.common.parsing.Metadata;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointTypeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StandEndpointTypeDto {
    private StandEndpointTypeId id;
    private String name;
    private Metadata inMeta;
    private Metadata outMeta;
}
