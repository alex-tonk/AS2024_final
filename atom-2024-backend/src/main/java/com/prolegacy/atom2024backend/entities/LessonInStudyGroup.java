package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.LessonDto;
import com.prolegacy.atom2024backend.entities.ids.LessonInStudyGroupId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class LessonInStudyGroup {
    @EmbeddedId
    private LessonInStudyGroupId id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    @MapsId("lessonId")
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    @MapsId("studyGroupId")
    private StudyGroup studyGroup;

    private Instant beginDate;
    private Instant endDate;
    @Column(columnDefinition = "text")
    private String description;

    public LessonInStudyGroup(Lesson lesson, StudyGroup studyGroup, LessonDto lessonDto) {
        this.id = new LessonInStudyGroupId(studyGroup.getId(), lesson.getId());
        this.lesson = lesson;
        this.studyGroup = studyGroup;
        this.update(lessonDto);
    }

    public void update(LessonDto lessonDto) {
        this.beginDate = lessonDto.getBeginDate();
        this.endDate = lessonDto.getEndDate();
        this.description = lessonDto.getDescription();
    }
}
