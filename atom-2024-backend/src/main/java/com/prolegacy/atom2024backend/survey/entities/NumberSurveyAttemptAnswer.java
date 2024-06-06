package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.survey.meta.answers.NumberSurveyQuestionAnswerMeta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.Optional;

@Entity
@DiscriminatorValue("NUMBER")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class NumberSurveyAttemptAnswer extends SurveyAttemptAnswer<NumberSurveyQuestionAnswerMeta> {
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private NumberSurveyQuestion question;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "answer")
    private NumberSurveyQuestionAnswerMeta answer;

    public NumberSurveyAttemptAnswer(SurveyAttempt surveyAttempt, NumberSurveyQuestion question, NumberSurveyQuestionAnswerMeta answer) {
        super(surveyAttempt);
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void updateAnswer(NumberSurveyQuestionAnswerMeta answer) {
        this.answer = answer;
    }

    @Override
    void checkCorrectness() {
        this.correct = Optional.ofNullable(this.answer)
                .map(NumberSurveyQuestionAnswerMeta::getAnswer)
                .map(answer -> Objects.equals(answer, question.getCorrectAnswerMeta().getAnswer()))
                .orElse(false);
    }
}
