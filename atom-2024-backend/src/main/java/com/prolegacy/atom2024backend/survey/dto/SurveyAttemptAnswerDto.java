package com.prolegacy.atom2024backend.survey.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptAnswerId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.enums.SurveyQuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SurveyAttemptAnswerDto {
    private SurveyAttemptAnswerId id;
    private SurveyAttemptId surveyAttemptId;
    private SurveyQuestionId questionId;
    private SurveyQuestionType type;
    private JsonNode answer;
}
