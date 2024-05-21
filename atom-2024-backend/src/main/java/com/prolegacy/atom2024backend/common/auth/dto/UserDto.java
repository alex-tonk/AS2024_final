package com.prolegacy.atom2024backend.common.auth.dto;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UserId id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String surname;
    private String phoneNumber;
    private Instant registrationDate;
    private Boolean archived;

    private String fullName;
    private String shortName;
    private String rolesAsString;
    private String availableStandEndpointsAsString;

    private List<StandEndpointDto> availableStandEndpoints;
    private List<RoleDto> roles;
}
