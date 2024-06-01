package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.CourseWithTutorsId;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class CourseWithTutors {
    @EmbeddedId
    private CourseWithTutorsId id;

    @ManyToOne
    @MapsId("studyGroupId")
    private StudyGroup studyGroup;
    @ManyToOne
    @MapsId("courseId")
    private Course course;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorInCourse> tutors = new ArrayList<>();

    public CourseWithTutors(StudyGroup studyGroup, Course course) {
        this.id = new CourseWithTutorsId(studyGroup.getId(), course.getId());
        this.studyGroup = studyGroup;
        this.course = course;
    }

    public List<TutorInCourse> getTutors() {
        return Collections.unmodifiableList(tutors);
    }

    public TutorInCourse addTutor(Tutor tutor) {
        if (tutors.stream().map(TutorInCourse::getTutor).map(Tutor::getId).anyMatch(tutorId -> tutorId.equals(tutor.getId()))) {
            throw new BusinessLogicException(
                    "Преподаватель [id=%s] уже преподает курс [id=%s] в учебной групе [id=%s]"
                            .formatted(tutor.getId(), course.getId(), studyGroup.getId())
            );
        }
        TutorInCourse tutorInCourse = new TutorInCourse(this, tutor);
        tutors.add(tutorInCourse);
        studyGroup.getChat().addMember(tutor.getUser());
        return tutorInCourse;
    }

    public void removeTutor(TutorId tutorId) {
        boolean removed = tutors.removeIf(tutor -> tutor.getTutor().getId().equals(tutorId));
        if (!removed) {
            throw new BusinessLogicException("Преподаватель [%s] не преподает курс [%s] в учебной групе [%s]"
                    .formatted(tutorId, course.getId(), studyGroup.getId())
            );
        }
    }
}
