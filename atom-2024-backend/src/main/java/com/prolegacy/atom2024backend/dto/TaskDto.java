package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.TaskId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    private BigDecimal numOfAttempts;
    private BigDecimal averageTime;
    private BigDecimal difficultyScore;

    private AttemptDto lastAttempt;
    private List<SupplementDto> supplements;

    public String getDifficultyLocale() {
        return Optional.ofNullable(this.difficulty)
                .map(d -> {
                    if (d.compareTo(BigDecimal.valueOf(1)) <= 0) {
                        return "Новичок";
                    } else if (d.compareTo(BigDecimal.valueOf(2)) <= 0) {
                        return "Ученик";
                    } else if (d.compareTo(BigDecimal.valueOf(3)) <= 0) {
                        return "Профессионал";
                    } else if (d.compareTo(BigDecimal.valueOf(4)) <= 0) {
                        return "Эксперт";
                    } else if (d.compareTo(BigDecimal.valueOf(5)) <= 0) {
                        return "Мастер";
                    }
                    return null;
                }).orElse(null);
    }
}
