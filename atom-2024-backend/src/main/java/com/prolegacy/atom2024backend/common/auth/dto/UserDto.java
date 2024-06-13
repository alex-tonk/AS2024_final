package com.prolegacy.atom2024backend.common.auth.dto;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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

    private TutorId tutorId;
    private StudentId studentId;

    private String fullName;
    private String shortName;
    private String rolesAsString;

    private Set<RoleDto> roles;
}
