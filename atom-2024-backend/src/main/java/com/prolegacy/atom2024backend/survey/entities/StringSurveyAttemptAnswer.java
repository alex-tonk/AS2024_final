package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.survey.meta.answers.StringSurveyQuestionAnswerMeta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Optional;

@Entity
@DiscriminatorValue("STRING")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class StringSurveyAttemptAnswer extends SurveyAttemptAnswer<StringSurveyQuestionAnswerMeta> {
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private StringSurveyQuestion question;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "answer")
    private StringSurveyQuestionAnswerMeta answer;

    public StringSurveyAttemptAnswer(SurveyAttempt surveyAttempt, StringSurveyQuestion question, StringSurveyQuestionAnswerMeta answer) {
        super(surveyAttempt);
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void updateAnswer(StringSurveyQuestionAnswerMeta answer) {
        this.answer = answer;
    }

    @Override
    void checkCorrectness() {
        this.correct = Optional.ofNullable(this.answer)
                .map(StringSurveyQuestionAnswerMeta::getAnswer)
                .map(
                        answer -> this.question.getCorrectAnswerMeta().getCorrectAnswers().stream()
                                .anyMatch(correctAnswer -> correctAnswer.trim().equalsIgnoreCase(answer.trim()))
                ).orElse(false);
    }
}
