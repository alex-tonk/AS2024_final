package com.prolegacy.atom2024backend.survey.meta.common;

import com.prolegacy.atom2024backend.common.annotation.SimpleQueryClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SimpleQueryClass
public class PredefinedAnswerQuestionMeta {
    private List<PredefinedAnswerMeta> answers;
}
