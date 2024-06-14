package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.enums.MarkEnum;
import com.prolegacy.atom2024backend.entities.ids.MarkId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Mark {
    @EmbeddedId
    private MarkId id;

    @Enumerated(EnumType.STRING)
    private MarkEnum mark;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    @MapsId("studyGroupId")
    private StudyGroup studyGroup;
    @ManyToOne
    @JoinColumn(name = "student_id")
    @MapsId("studentId")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    @MapsId("lessonId")
    private Lesson lesson;

    public Mark(LessonInStudyGroup lessonInStudyGroup, Student student, MarkEnum mark) {
        this.studyGroup = lessonInStudyGroup.getStudyGroup();
        this.lesson = lessonInStudyGroup.getLesson();
        this.student = student;
    }
}
