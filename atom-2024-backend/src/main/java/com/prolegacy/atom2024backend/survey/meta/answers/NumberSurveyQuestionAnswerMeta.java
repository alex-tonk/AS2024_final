package com.prolegacy.atom2024backend.survey.meta.answers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberSurveyQuestionAnswerMeta {
    private BigDecimal answer;
}
