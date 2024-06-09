package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TutorInCourseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class TutorInCourse {
    @EmbeddedId
    private TutorInCourseId id;

    @ManyToOne
    @MapsId("courseWithTutorsId")
    @JoinColumns({
            @JoinColumn(name = "course_with_tutors_course_id", nullable = false),
            @JoinColumn(name = "course_with_tutors_study_group_id", nullable = false)
    })
    private CourseWithTutors courseWithTutors;
    @ManyToOne
    @MapsId("tutorId")
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    public TutorInCourse(CourseWithTutors courseWithTutors, Tutor tutor) {
        this.id = new TutorInCourseId(courseWithTutors.getId(), tutor.getId());
        this.courseWithTutors = courseWithTutors;
        this.tutor = tutor;
    }
}
