package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.meta.answers.StringSurveyQuestionCorrectAnswerMeta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@DiscriminatorValue("STRING")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class StringSurveyQuestion extends SurveyQuestion {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "correct_answer_meta")
    private StringSurveyQuestionCorrectAnswerMeta correctAnswerMeta;

    public StringSurveyQuestion(Survey survey, File file, SurveyQuestionDto questionDto, StringSurveyQuestionCorrectAnswerMeta correctAnswerMeta) {
        super(survey, file, questionDto);
        this.validateCorrectAnswerMeta(correctAnswerMeta);
        this.correctAnswerMeta = correctAnswerMeta;
    }

    private void validateCorrectAnswerMeta(StringSurveyQuestionCorrectAnswerMeta correctAnswerMeta) {
        boolean noAnswersProvided = Optional.ofNullable(correctAnswerMeta)
                .map(StringSurveyQuestionCorrectAnswerMeta::getCorrectAnswers)
                .map(List::isEmpty)
                .orElse(true);
        if (noAnswersProvided) {
            throw new BusinessLogicException("Не задано ни одного варианта ответа");
        }
        boolean anyAnswerIsNull = correctAnswerMeta.getCorrectAnswers().stream()
                .anyMatch(Objects::isNull);
        if (anyAnswerIsNull) {
            throw new BusinessLogicException("Один из вариантов ответа некорректен");
        }
    }
}
