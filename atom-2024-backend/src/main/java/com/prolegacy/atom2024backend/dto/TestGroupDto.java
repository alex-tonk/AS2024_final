package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TestGroupDto {
    private TestGroupId id;
    private String comment;
    private Instant startDate;
    private Instant endDate;
    private UserDto startUser;
    private List<TestDto> tests;

    private Long executionSeconds;

    private Boolean isBusy;
    private Boolean hasError;
    private Boolean isStopped;
}
