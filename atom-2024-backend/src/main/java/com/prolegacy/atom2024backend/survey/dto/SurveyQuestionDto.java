package com.prolegacy.atom2024backend.survey.dto;

import com.fasterxml.jackson.databind.JsonNode;
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
public class SurveyQuestionDto {
    private SurveyQuestionId id;
    private SurveyQuestionType type;
    private String wording;
    private String comment;
    private Long orderNumber;
    private JsonNode meta;
    private JsonNode correctAnswerMeta;
}
