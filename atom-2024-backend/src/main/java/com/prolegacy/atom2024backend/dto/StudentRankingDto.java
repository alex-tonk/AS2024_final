package com.prolegacy.atom2024backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentRankingDto {
    private String fullName;
    private Long rank;
    private Double mark;
    private Long totalMark;
    private Long totalCompleteTimeSeconds;
    private Long completeTimeSeconds;
    private Long completeTaskCount;

    public String getTotalCompleteTimeSecondsLocale() {
        long minutes = totalCompleteTimeSeconds / 60;
        long seconds = totalCompleteTimeSeconds % 60;
        String result = "";
        if (minutes > 0) {
            result = "%s мин. ".formatted(minutes);
        }
        result += "%s сек.".formatted(seconds);
        return result;
    }

    public String getCompleteTimeSecondsLocale() {
        long minutes = completeTimeSeconds / 60;
        long seconds = completeTimeSeconds % 60;
        String result = "";
        if (minutes > 0) {
            result = "%s мин. ".formatted(minutes);
        }
        result += "%s сек.".formatted(seconds);
        return result;
    }
}
