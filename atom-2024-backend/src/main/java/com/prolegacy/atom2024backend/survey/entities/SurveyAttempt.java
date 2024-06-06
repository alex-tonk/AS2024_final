package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor
public class SurveyAttempt {

    @Id
    @GeneratedValue(generator = "typed-sequence")
    private SurveyAttemptId id;

    private Instant beginDate;
    private Instant finishDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Setter(AccessLevel.PUBLIC)
    private Boolean lastAttempt;

    @OneToMany(mappedBy = "surveyAttempt", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SurveyAttemptAnswer<?>> answers = new ArrayList<>();

    public SurveyAttempt(User user, Survey survey) {
        this.user = user;
        this.survey = survey;
        this.beginDate = Instant.now();
        this.lastAttempt = true;
    }

    public List<SurveyAttemptAnswer<?>> getAnswers() {
        return Collections.unmodifiableList(this.answers);
    }

    public void addAnswer(SurveyAttemptAnswer<?> answer) {
        if (finishDate != null) {
            throw new BusinessLogicException("Попытка уже завершена");
        }
        if (this.answers.stream().anyMatch(a -> Objects.equals(answer.getQuestion().getId(), a.getQuestion().getId()))) {
            throw new BusinessLogicException("Ответ на данный вопрос уже присутствует");
        }
        if (!Objects.equals(this.survey.getId(), answer.getQuestion().getSurvey().getId())) {
            throw new BusinessLogicException("Ответ не соответствует проходимому тестированию");
        }
        this.answers.add(answer);
    }

    public void finish() {
        this.finishDate = Instant.now();
        this.answers.forEach(SurveyAttemptAnswer::checkCorrectness);
    }

    public Optional<SurveyAttemptAnswer<?>> getAnswerForQuestion(SurveyQuestionId questionId) {
        return this.answers.stream().filter(a -> Objects.equals(a.getQuestion().getId(), questionId)).findFirst();
    }
}
