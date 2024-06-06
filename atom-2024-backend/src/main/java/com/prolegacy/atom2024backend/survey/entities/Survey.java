package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.survey.dto.SurveyDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor
public class Survey {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private SurveyId id;

    @Column(nullable = false)
    private String name;
    private Long timeLimitMinutes;

    @OneToMany(mappedBy = "survey", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SurveyQuestion> questions = new ArrayList<>();

    public Survey(SurveyDto surveyDto) {
        this.update(surveyDto);
    }

    public List<SurveyQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public void update(SurveyDto surveyDto) {
        if (surveyDto.getName() == null) {
            throw new BusinessLogicException("Не задано наименование тестирования");
        }
        if (surveyDto.getTimeLimitMinutes() != null && surveyDto.getTimeLimitMinutes() <= 0L) {
            throw new BusinessLogicException("Временной лимит не может быть отрицательным или нулевым");
        }
        this.name = surveyDto.getName();
        this.timeLimitMinutes = surveyDto.getTimeLimitMinutes();
    }

    public void addQuestion(SurveyQuestion surveyQuestion) {
        surveyQuestion.setOrderNumber(
                this.questions.stream()
                        .map(SurveyQuestion::getOrderNumber)
                        .max(Comparator.comparingLong(o -> o))
                        .orElse(0L) + 1L
        );
        this.questions.add(surveyQuestion);
    }

    public void clearQuestions() {
        this.questions.clear();
    }

    public Optional<SurveyQuestion> getQuestion(SurveyQuestionId questionId) {
        return this.questions.stream().filter(q -> Objects.equals(q.getId(), questionId)).findFirst();
    }
}
