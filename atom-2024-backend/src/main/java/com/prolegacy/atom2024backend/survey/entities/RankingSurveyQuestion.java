package com.prolegacy.atom2024backend.survey.entities;

import com.google.common.collect.Sets;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.meta.answers.RankingSurveyQuestionAnswerMeta;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("RANKING")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RankingSurveyQuestion extends SurveyQuestion {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "meta")
    private PredefinedAnswerQuestionMeta meta = null;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "correct_answer_meta")
    private RankingSurveyQuestionAnswerMeta correctAnswerMeta;

    public RankingSurveyQuestion(Survey survey, File file, SurveyQuestionDto questionDto, PredefinedAnswerQuestionMeta meta, RankingSurveyQuestionAnswerMeta correctAnswerMeta) {
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

    private void validateCorrectAnswerMeta(PredefinedAnswerQuestionMeta meta, RankingSurveyQuestionAnswerMeta correctAnswerMeta) {
        boolean someAnswersNotContainedInRightOrder = !Sets.symmetricDifference(
                new HashSet<>(
                        Optional.ofNullable(correctAnswerMeta)
                                .map(RankingSurveyQuestionAnswerMeta::getAnswerIdsOrdered)
                                .orElseGet(ArrayList::new)
                ),
                meta.getAnswers().stream().map(PredefinedAnswerMeta::getId).collect(Collectors.toSet())
        ).isEmpty();

        if (someAnswersNotContainedInRightOrder) {
            throw new BusinessLogicException("Некорректный правильный порядок");
        }
    }
}
