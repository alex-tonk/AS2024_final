package com.prolegacy.atom2024backend.survey.meta.answers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingSurveyQuestionAnswerMeta {
    List<Long> answerIdsOrdered;
}
