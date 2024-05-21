package com.prolegacy.atom2024backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PersonalDto {
    public static PersonalDto EMPTY = new PersonalDto(-1L, null);

    private Long id;
    private String caption;
}
