package com.prolegacy.atom2024backend.survey.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.enums.SurveyQuestionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Table(name = "survey_question")
public abstract class SurveyQuestion {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    protected SurveyQuestionId id;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    protected Survey survey;
    protected FileId fileId;
    @Column(nullable = false, columnDefinition = "text")
    protected String wording;
    @Column(columnDefinition = "text")
    protected String comment;
    @Column(nullable = false)
    @Setter(AccessLevel.PACKAGE)
    protected Long orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", insertable = false, updatable = false)
    protected SurveyQuestionType type;

    public SurveyQuestion(Survey survey, File file, SurveyQuestionDto questionDto) {
        this.survey = survey;

        if (questionDto.getWording() == null) {
            throw new BusinessLogicException("Не задана формулировка вопроса");
        }

        this.fileId = Optional.ofNullable(file).map(File::getId).orElse(null);
        this.wording = questionDto.getWording();
        this.comment = questionDto.getComment();
    }
}
