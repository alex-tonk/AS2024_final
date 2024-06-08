package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.entities.chat.Chat;
import com.prolegacy.atom2024backend.entities.ids.*;
import com.prolegacy.atom2024backend.exceptions.CourseNotFoundException;
import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;

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

    private String name;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseWithTutors> courses = new ArrayList<>();
    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentInGroup> students = new ArrayList<>();

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Chat chat;

    public List<StudentInGroup> getStudents() {
        return Collections.immutable(students);
    }

    public StudyGroup(StudyGroupDto studyGroupDto) {
        update(studyGroupDto);
        this.chat = new Chat(
                ChatDto.builder()
                        .name("Чат группы %s".formatted(this.name))
                        .build(),
                this
        );
    }

    public void update(StudyGroupDto studyGroupDto) {
        this.name = studyGroupDto.getName();
    }

    public StudentInGroup addStudent(Student student) {
        if (students.stream().map(StudentInGroup::getId).map(StudentInGroupId::getStudentId).anyMatch(id -> id.equals(student.getId()))) {
            throw new BusinessLogicException("Студент [id=%s] уже зачислен в эту группу".formatted(student.getId()));
        }

        StudentInGroup studentInGroup = new StudentInGroup(this, student);
        students.add(studentInGroup);
        chat.addMember(student.getUser());
        return studentInGroup;
    }

    public void removeStudents(Collection<StudentId> studentsIds) {
        this.students.removeIf(s -> studentsIds.contains(s.getId().getStudentId()));
    }

    public CourseWithTutors addCourse(Course course) {
        if (this.courses.stream()
                .map(CourseWithTutors::getCourse)
                .map(Course::getId)
                .anyMatch(course.getId()::equals)) {
            throw new BusinessLogicException("Курс [id=%s] уже добален в учебную группу [id=%s]".formatted(course.getId(), id));
        }
        CourseWithTutors courseWithTutors = new CourseWithTutors(this, course);
        courses.add(courseWithTutors);
        return courseWithTutors;
    }

    public void removeCourse(CourseId courseId) {
        throw new NotImplementedException("Пока не уверен, как удалять/архивировать");
    }

    public TutorInCourse addTutor(CourseId courseId, Tutor tutor) {
        CourseWithTutors course = this.courses.stream().filter(c -> c.getId().getCourseId().equals(courseId)).findFirst()
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        return course.addTutor(tutor);
    }

    public void removeTutor(TutorId tutorId, CourseId courseId) {
        this.courses.stream().filter(c -> c.getId().getCourseId().equals(courseId))
                .findFirst()
                .ifPresentOrElse(
                        courseWithTeachers -> courseWithTeachers.removeTutor(tutorId),
                        () -> {
                            throw new CourseNotFoundException(courseId);
                        }
                );
    }
}
