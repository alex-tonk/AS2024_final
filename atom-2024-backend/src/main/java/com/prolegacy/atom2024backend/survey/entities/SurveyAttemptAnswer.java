package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptAnswerId;
import com.prolegacy.atom2024backend.survey.enums.SurveyQuestionType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Table(name = "survey_attempt_answer")
public abstract class SurveyAttemptAnswer<T> {

    @Id
    @GeneratedValue(generator = "typed-sequence")
    private SurveyAttemptAnswerId id;

    @ManyToOne
    @JoinColumn(name = "survey_attempt_id", nullable = false)
    protected SurveyAttempt surveyAttempt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", insertable = false, updatable = false)
    protected SurveyQuestionType type;

    protected Boolean correct;

    public SurveyAttemptAnswer(SurveyAttempt surveyAttempt) {
        this.surveyAttempt = surveyAttempt;
    }

    public abstract SurveyQuestion getQuestion();
    public abstract void updateAnswer(T answer);
    abstract void checkCorrectness();
}
