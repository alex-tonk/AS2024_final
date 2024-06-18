package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.TaskId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskDto {
    private TaskId id;
    private String code;
    private String title;
    private String content;
    private BigDecimal difficulty;
    private Integer time;

    private BigDecimal difficultyScore;
    private AttemptDto lastAttempt;
    private List<SupplementDto> supplements;
}
