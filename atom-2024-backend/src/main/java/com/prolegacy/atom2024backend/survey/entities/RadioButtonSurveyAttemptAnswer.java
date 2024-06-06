package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.survey.meta.answers.RadioButtonSurveyQuestionAnswerMeta;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.Optional;

@Entity
@DiscriminatorValue("RADIO_BUTTON")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class RadioButtonSurveyAttemptAnswer extends SurveyAttemptAnswer<RadioButtonSurveyQuestionAnswerMeta> {
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private RadioButtonSurveyQuestion question;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "answer")
    private RadioButtonSurveyQuestionAnswerMeta answer;

    public RadioButtonSurveyAttemptAnswer(SurveyAttempt surveyAttempt, RadioButtonSurveyQuestion question, RadioButtonSurveyQuestionAnswerMeta answer) {
        super(surveyAttempt);
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void updateAnswer(RadioButtonSurveyQuestionAnswerMeta answer) {
        this.answer = answer;
    }

    @Override
    void checkCorrectness() {
        this.correct = Optional.ofNullable(this.answer)
                .map(RadioButtonSurveyQuestionAnswerMeta::getAnswerId)
                .map(answerId -> Objects.equals(answerId, this.question.getCorrectAnswerMeta().getAnswerId()))
                .orElse(false);
    }
}
