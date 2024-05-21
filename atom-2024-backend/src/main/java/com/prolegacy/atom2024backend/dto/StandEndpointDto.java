package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StandEndpointDto {
    private StandEndpointId id;
    private StandDto stand;
    private StandEndpointTypeDto standEndpointType;
    private String name;
    private String description;
    private String url;
    private String jsCode;
}
