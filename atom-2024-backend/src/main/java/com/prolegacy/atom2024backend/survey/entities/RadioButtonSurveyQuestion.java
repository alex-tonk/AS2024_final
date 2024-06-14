package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.meta.answers.RadioButtonSurveyQuestionAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerQuestionMeta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@DiscriminatorValue("RADIO_BUTTON")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RadioButtonSurveyQuestion extends SurveyQuestion {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "meta")
    private PredefinedAnswerQuestionMeta meta = null;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "correct_answer_meta")
    private RadioButtonSurveyQuestionAnswerMeta correctAnswerMeta;


    public RadioButtonSurveyQuestion(Survey survey,
                                     File file,
                                     SurveyQuestionDto questionDto,
                                     PredefinedAnswerQuestionMeta meta,
                                     RadioButtonSurveyQuestionAnswerMeta correctAnswerMeta) {
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

    private void validateCorrectAnswerMeta(PredefinedAnswerQuestionMeta meta, RadioButtonSurveyQuestionAnswerMeta correctAnswerMeta) {
        if (correctAnswerMeta == null || correctAnswerMeta.getAnswerId() == null) {
            throw new BusinessLogicException("Не задан правильный ответ");
        }

        boolean correctAnswersNotContained = meta.getAnswers().stream()
                .map(PredefinedAnswerMeta::getId)
                .noneMatch(id -> Objects.equals(id, correctAnswerMeta.getAnswerId()));

        if (correctAnswersNotContained) {
            throw new BusinessLogicException("Правильный ответ не содержится в вариантах ответа");
        }
    }
}
