package com.prolegacy.atom2024backend.common.auth.dto;

import com.prolegacy.atom2024backend.common.auth.entities.id.RoleId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private RoleId id;
    private String name;
    private String localeName;
}
