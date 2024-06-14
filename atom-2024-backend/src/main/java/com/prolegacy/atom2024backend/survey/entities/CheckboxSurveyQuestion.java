package com.prolegacy.atom2024backend.survey.entities;

import com.google.common.collect.Sets;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.meta.answers.CheckboxSurveyQuestionAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerQuestionMeta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("CHECKBOX")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class CheckboxSurveyQuestion extends SurveyQuestion {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "meta")
    private PredefinedAnswerQuestionMeta meta = null;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "correct_answer_meta")
    private CheckboxSurveyQuestionAnswerMeta correctAnswerMeta;

    public CheckboxSurveyQuestion(Survey survey, File file, SurveyQuestionDto questionDto, PredefinedAnswerQuestionMeta meta, CheckboxSurveyQuestionAnswerMeta correctAnswerMeta) {
        super(survey, file, questionDto);

        this.validateMeta(meta);
        this.validateCorrectAnswerMeta(meta, correctAnswerMeta);

        this.meta = meta;
        this.correctAnswerMeta = correctAnswerMeta;
    }

    private void validateMeta(PredefinedAnswerQuestionMeta meta) {
        boolean noAnswersProvided = Optional.ofNullable(meta)
                .map(PredefinedAnswerQuestionMeta::getAnswers)
                .map(List::isEmpty)
                .orElse(true);
        if (noAnswersProvided) {
            throw new BusinessLogicException("Не задано ни одного варианта ответа");
        }
        if (meta.getAnswers().stream().anyMatch(a -> a.getValue() == null)) {
            throw new BusinessLogicException("У варианта ответа отсутствует формулировка");
        }
    }

    private void validateCorrectAnswerMeta(PredefinedAnswerQuestionMeta meta, CheckboxSurveyQuestionAnswerMeta correctAnswerMeta) {
        boolean noAnswersProvided = Optional.ofNullable(correctAnswerMeta)
                .map(CheckboxSurveyQuestionAnswerMeta::getAnswerIds)
                .map(List::isEmpty)
                .orElse(true);
        if (noAnswersProvided) {
            throw new BusinessLogicException("Не задано ни одного правильного ответа");
        }

        boolean correctAnswersNotContained = !Sets.difference(
                new HashSet<>(correctAnswerMeta.getAnswerIds()),
                meta.getAnswers().stream().map(PredefinedAnswerMeta::getId).collect(Collectors.toSet())
        ).isEmpty();
        if (correctAnswersNotContained) {
            throw new BusinessLogicException("Правильный ответ не содержится в вариантах ответа");
        }
    }
}
