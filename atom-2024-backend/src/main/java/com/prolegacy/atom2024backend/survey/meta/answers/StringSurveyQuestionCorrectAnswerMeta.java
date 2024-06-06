package com.prolegacy.atom2024backend.survey.meta.answers;

import com.prolegacy.atom2024backend.common.annotation.SimpleQueryClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@SimpleQueryClass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringSurveyQuestionCorrectAnswerMeta {
    private List<String> correctAnswers;
}
