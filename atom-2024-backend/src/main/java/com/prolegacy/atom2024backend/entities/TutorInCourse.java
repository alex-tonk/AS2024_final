package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TutorInCourseId;
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
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class TutorInCourse {
    @EmbeddedId
    private TutorInCourseId id;

    @ManyToOne
    @MapsId("courseWithTutorsId")
    private CourseWithTutors courseWithTutors;
    @ManyToOne
    @MapsId("tutorId")
    private Tutor tutor;

    public TutorInCourse(CourseWithTutors courseWithTutors, Tutor tutor) {
        this.id = new TutorInCourseId(courseWithTutors.getId(), tutor.getId());
        this.courseWithTutors = courseWithTutors;
        this.tutor = tutor;
    }
}
