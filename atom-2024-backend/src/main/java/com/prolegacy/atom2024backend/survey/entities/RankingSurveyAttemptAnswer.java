package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.survey.meta.answers.RankingSurveyQuestionAnswerMeta;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Optional;

@Entity
@DiscriminatorValue("RANKING")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RankingSurveyAttemptAnswer extends SurveyAttemptAnswer<RankingSurveyQuestionAnswerMeta> {
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private RankingSurveyQuestion question;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "answer")
    private RankingSurveyQuestionAnswerMeta answer;

    public RankingSurveyAttemptAnswer(SurveyAttempt surveyAttempt, RankingSurveyQuestion question, RankingSurveyQuestionAnswerMeta answer) {
        super(surveyAttempt);
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void updateAnswer(RankingSurveyQuestionAnswerMeta answer) {
        this.answer = answer;
    }

    @Override
    void checkCorrectness() {
        this.correct = Optional.ofNullable(this.answer)
                .map(RankingSurveyQuestionAnswerMeta::getAnswerIdsOrdered)
                .map(answerIds -> new ArrayList<>(answerIds).equals(this.question.getCorrectAnswerMeta().getAnswerIdsOrdered()))
                .orElse(false);
    }
}
