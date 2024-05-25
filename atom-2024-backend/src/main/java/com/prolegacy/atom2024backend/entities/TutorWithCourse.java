package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TutorWithCourseId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class TutorWithCourse {
    @EmbeddedId
    private TutorWithCourseId id;

    @ManyToOne
    @MapsId("studyGroupId")
    private StudyGroup studyGroup;
    @ManyToOne
    @MapsId("tutorId")
    private Tutor tutor;
    @ManyToOne
    @MapsId("courseId")
    private Course course;

    public TutorWithCourse(StudyGroup studyGroup, Tutor tutor, Course course) {
        this.id = new TutorWithCourseId(studyGroup.getId(), tutor.getId(), course.getId());
        this.studyGroup = studyGroup;
        this.tutor = tutor;
        this.course = course;
    }
}
