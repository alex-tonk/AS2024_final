package com.prolegacy.atom2024backend.survey.meta.answers;

import com.prolegacy.atom2024backend.common.annotation.SimpleQueryClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SimpleQueryClass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringSurveyQuestionAnswerMeta {
    private String answer;
}
