package com.prolegacy.atom2024backend.survey.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptAnswerId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.enums.SurveyQuestionType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Subselect;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@Subselect("""
        select * from survey_attempt_answer
        """)
public class SurveyAttemptAnswerView {
    @Id
    private SurveyAttemptAnswerId id;
    private SurveyAttemptId surveyAttemptId;
    private SurveyQuestionId questionId;

    private Boolean correct;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", insertable = false, updatable = false)
    protected SurveyQuestionType type;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode answer;
}
