package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.TutorWithCourseDto;
import com.prolegacy.atom2024backend.entities.*;
import com.prolegacy.atom2024backend.entities.ids.CourseId;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import com.prolegacy.atom2024backend.entities.ids.StudyGroupId;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import com.prolegacy.atom2024backend.exceptions.CourseNotFoundException;
import com.prolegacy.atom2024backend.exceptions.StudentNotFoundException;
import com.prolegacy.atom2024backend.exceptions.StudyGroupNotFoundException;
import com.prolegacy.atom2024backend.exceptions.TutorNotFoundException;
import com.prolegacy.atom2024backend.readers.StudyGroupReader;
import com.prolegacy.atom2024backend.repositories.CourseRepository;
import com.prolegacy.atom2024backend.repositories.StudentRepository;
import com.prolegacy.atom2024backend.repositories.StudyGroupRepository;
import com.prolegacy.atom2024backend.repositories.TutorRepository;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class StudyGroupService {
    @Autowired
    private StudyGroupRepository studyGroupRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudyGroupReader studyGroupReader;

    public StudyGroupDto createStudyGroup(StudyGroupDto studyGroupDto) {
        StudyGroup studyGroup = studyGroupRepository.save(new StudyGroup(studyGroupDto));
        for (StudentInGroupDto studentInGroupDto : Optional.ofNullable(studyGroupDto.getStudents()).orElseGet(ArrayList::new)) {
            addStudent(studyGroup.getId(), studentInGroupDto.getStudent().getId());
        }

        return studyGroupReader.getStudyGroup(studyGroup.getId());
    }

    public StudyGroupDto updateStudyGroup(StudyGroupId studyGroupId, StudyGroupDto studyGroupDto) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));

        studyGroup.update(studyGroupDto);

        return studyGroupReader.getStudyGroup(studyGroupId);
    }

    public StudentInGroupDto addStudent(StudyGroupId studyGroupId, StudentId studentId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId).orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException(studentId));

        StudentInGroup studentInGroup = studyGroup.addStudent(student);

        return studyGroupReader.getStudent(studentInGroup.getId());
    }

    public void removeStudent(StudyGroupId studyGroupId, StudentId studentId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId).orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
        studyGroup.removeStudents(Collections.of(studentId));
    }

    public TutorWithCourseDto addTutor(StudyGroupId studyGroupId, TutorId tutorId, CourseId courseId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId).orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
        Tutor tutor = tutorRepository.findById(tutorId).orElseThrow(() -> new TutorNotFoundException(tutorId));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));

        TutorWithCourse tutorWithCourse = studyGroup.addTutor(tutor, course);

        return studyGroupReader.getTutor(tutorWithCourse.getId());
    }

    public void removeTutor(StudyGroupId studyGroupId, TutorId tutorId, CourseId courseId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId).orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
        studyGroup.removeTutor(tutorId, courseId);
    }
}
