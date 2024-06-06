package com.prolegacy.atom2024backend.survey.dto;

import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SurveyDto {
    private SurveyId id;
    private String name;
    private Long timeLimitMinutes;
    private Long questionCount;
    private SurveyAttemptDto lastSurveyAttempt;

    private List<SurveyQuestionDto> questions;
}
