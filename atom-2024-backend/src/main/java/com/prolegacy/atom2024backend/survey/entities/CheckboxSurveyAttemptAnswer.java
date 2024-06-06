package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.survey.meta.answers.CheckboxSurveyQuestionAnswerMeta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Optional;

@Entity
@DiscriminatorValue("CHECKBOX")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class CheckboxSurveyAttemptAnswer extends SurveyAttemptAnswer<CheckboxSurveyQuestionAnswerMeta> {
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private CheckboxSurveyQuestion question;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "answer")
    private CheckboxSurveyQuestionAnswerMeta answer;

    public CheckboxSurveyAttemptAnswer(SurveyAttempt surveyAttempt, CheckboxSurveyQuestion question, CheckboxSurveyQuestionAnswerMeta answer) {
        super(surveyAttempt);
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void updateAnswer(CheckboxSurveyQuestionAnswerMeta answer) {
        this.answer = answer;
    }

    @Override
    void checkCorrectness() {
        this.correct = Optional.ofNullable(this.answer)
                .map(CheckboxSurveyQuestionAnswerMeta::getAnswerIds)
                .map(
                        answerIds -> new HashSet<>(answerIds).equals(new HashSet<>(question.getCorrectAnswerMeta().getAnswerIds()))
                ).orElse(false);
    }
}
