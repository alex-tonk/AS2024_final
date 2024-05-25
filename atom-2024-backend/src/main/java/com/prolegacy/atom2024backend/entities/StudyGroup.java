package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.entities.ids.*;
import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class StudyGroup {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private StudyGroupId id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorWithCourse> tutors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentInGroup> students = new ArrayList<>();

    public List<StudentInGroup> getStudents() {
        return Collections.immutable(students);
    }

    public StudyGroup(StudyGroupDto studyGroupDto) {
        update(studyGroupDto);
    }

    public void update(StudyGroupDto studyGroupDto) {

    }

    public StudentInGroup addStudent(Student student) {
        if (students.stream().map(StudentInGroup::getId).map(StudentInGroupId::getStudentId).anyMatch(id -> id.equals(student.getId()))) {
            throw new BusinessLogicException("Студент [id=%s] уже зачислен в эту группу".formatted(student.getId()));
        }

        StudentInGroup studentInGroup = new StudentInGroup(this, student);
        students.add(studentInGroup);
        return studentInGroup;
    }

    public void removeStudents(Collection<StudentId> studentsIds) {
        this.students.removeIf(s -> studentsIds.contains(s.getId().getStudentId()));
    }

    public TutorWithCourse addTutor(Tutor tutor, Course course) {
        if (tutors.stream().map(TutorWithCourse::getId).anyMatch(id -> id.getCourseId().equals(course.getId()) && id.getTutorId().equals(tutor.getId()))) {
            throw new BusinessLogicException("Преподаватель с таким предметом уже назначен в эту группу");
        }

        TutorWithCourse tutorWithCourse = new TutorWithCourse(this, tutor, course);
        this.tutors.add(tutorWithCourse);
        return tutorWithCourse;
    }

    public void removeTutor(TutorId tutorId, CourseId courseId) {
        this.tutors.removeIf(t -> t.getId().getTutorId().equals(tutorId) && t.getId().getCourseId().equals(courseId));
    }
}
