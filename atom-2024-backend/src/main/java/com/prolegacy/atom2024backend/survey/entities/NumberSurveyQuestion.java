package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.meta.answers.NumberSurveyQuestionAnswerMeta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@DiscriminatorValue("NUMBER")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class NumberSurveyQuestion extends SurveyQuestion {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "correct_answer_meta")
    private NumberSurveyQuestionAnswerMeta correctAnswerMeta;

    public NumberSurveyQuestion(Survey survey, File file, SurveyQuestionDto questionDto, NumberSurveyQuestionAnswerMeta correctAnswerMeta) {
        super(survey, file, questionDto);

        this.validateCorrectAnswerMeta(correctAnswerMeta);

        this.correctAnswerMeta = correctAnswerMeta;
    }

    private void validateCorrectAnswerMeta(NumberSurveyQuestionAnswerMeta correctAnswerMeta) {
        if (correctAnswerMeta == null || correctAnswerMeta.getAnswer() == null) {
            throw new BusinessLogicException("Не задан правильный ответ");
        }
    }
}
