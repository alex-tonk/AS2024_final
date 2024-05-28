package com.prolegacy.atom2024backend.entities.ids;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TutorInCourseId implements Serializable {
    CourseWithTutorsId courseWithTutorsId;
    TutorId tutorId;

    public TutorInCourseId(StudyGroupId studyGroupId, CourseId courseId, TutorId tutorId) {
        this.courseWithTutorsId = new CourseWithTutorsId(studyGroupId, courseId);
        this.tutorId = tutorId;
    }
}
