package com.prolegacy.atom2024backend.survey.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.enums.SurveyQuestionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Subselect;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@Subselect("""
        select * from survey_question
        """)
public class SurveyQuestionView {
    @Id
    private SurveyQuestionId id;
    private SurveyId surveyId;
    private FileId fileId;
    private String wording;
    private String comment;
    private Long orderNumber;
    @Enumerated(EnumType.STRING)
    private SurveyQuestionType type;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode meta;
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode correctAnswerMeta;
}
