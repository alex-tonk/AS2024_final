package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.dto.CourseWithTutorsDto;
import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.TutorInCourseDto;
import com.prolegacy.atom2024backend.entities.ids.*;
import com.prolegacy.atom2024backend.exceptions.CourseNotFoundException;
import com.prolegacy.atom2024backend.exceptions.StudentNotFoundException;
import com.prolegacy.atom2024backend.exceptions.StudyGroupNotFoundException;
import com.prolegacy.atom2024backend.exceptions.TutorNotFoundException;
import com.prolegacy.atom2024backend.readers.StudyGroupReader;
import com.prolegacy.atom2024backend.services.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("study-groups")
@TypescriptEndpoint
public class StudyGroupController {
    @Autowired
    private StudyGroupReader studyGroupReader;
    @Autowired
    private StudyGroupService studyGroupService;
    @Autowired
    private UserReader userReader;
    @Autowired
    private UserProvider userProvider;

    @GetMapping("{studyGroupId}")
    public StudyGroupDto getStudyGroup(@PathVariable StudyGroupId studyGroupId) {
        return Optional.ofNullable(studyGroupReader.getStudyGroup(studyGroupId))
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
    }

    @GetMapping
    public List<StudyGroupDto> getStudyGroups() {
        return studyGroupReader.getStudyGroups();
    }

    @GetMapping("tutor-study-groups")
    public List<StudyGroupDto> getStudyGroupsForTutor() {
        UserDto user = userReader.getUser(userProvider.get().getId());
        if (user.getTutorId() == null) throw new BusinessLogicException("Вы не являетесь преподавателем");
        return studyGroupReader.getStudyGroupsForTutor(user.getTutorId());
    }

    @GetMapping("student-study-groups")
    public List<StudyGroupDto> getStudyGroupsForStudent() {
        UserDto user = userReader.getUser(userProvider.get().getId());
        if (user.getStudentId() == null) throw new BusinessLogicException("Вы не являетесь студентом");
        return studyGroupReader.getStudyGroupsForStudent(user.getStudentId());
    }

    @PostMapping("search")
    public PageResponse<StudyGroupDto> searchStudyGroups(@RequestBody PageQuery pageQuery) {
        return studyGroupReader.searchStudyGroups(pageQuery);
    }

    @PostMapping
    public StudyGroupDto createStudyGroup(@RequestBody StudyGroupDto studyGroupDto) {
        return studyGroupService.createStudyGroup(studyGroupDto);
    }

    @PutMapping("{studyGroupId}")
    public StudyGroupDto updateStudyGroup(@PathVariable StudyGroupId studyGroupId, @RequestBody StudyGroupDto studyGroupDto) {
        return studyGroupService.updateStudyGroup(studyGroupId, studyGroupDto);
    }

    @GetMapping("{studyGroupId}/students/{studentId}")
    public StudentInGroupDto getStudent(@PathVariable StudyGroupId studyGroupId, @PathVariable StudentId studentId) {
        return Optional.ofNullable(studyGroupReader.getStudent(new StudentInGroupId(studyGroupId, studentId)))
                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    @GetMapping("{studyGroupId}/students")
    public List<StudentInGroupDto> getStudents(@PathVariable StudyGroupId studyGroupId) {
        return studyGroupReader.getStudents(studyGroupId);
    }

    @PostMapping("{studyGroupId}/students/search")
    public PageResponse<StudentInGroupDto> searchStudents(@PathVariable StudyGroupId studyGroupId, @RequestBody PageQuery pageQuery) {
        return studyGroupReader.searchStudents(studyGroupId, pageQuery);
    }

    @PostMapping("{studyGroupId}/students/{studentId}")
    public StudentInGroupDto addStudent(@PathVariable StudyGroupId studyGroupId, @PathVariable StudentId studentId) {
        return studyGroupService.addStudent(studyGroupId, studentId);
    }

    @DeleteMapping("{studyGroupId}/students/{studentId}")
    public void removeStudent(@PathVariable StudyGroupId studyGroupId, @PathVariable StudentId studentId) {
        studyGroupService.removeStudent(studyGroupId, studentId);
    }

    @GetMapping("{studyGroupId}/courses/{courseId}")
    public CourseWithTutorsDto getCourse(@PathVariable StudyGroupId studyGroupId, @PathVariable CourseId courseId) {
        return Optional.ofNullable(studyGroupReader.getCourse(new CourseWithTutorsId(studyGroupId, courseId)))
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @GetMapping("{studyGroupId}/courses")
    public List<CourseWithTutorsDto> getCourses(@PathVariable StudyGroupId studyGroupId) {
        return studyGroupReader.getCourses(studyGroupId);
    }

    @GetMapping("{studyGroupId}/courses/for-tutor")
    public List<CourseWithTutorsDto> getCoursesForTutor(@PathVariable StudyGroupId studyGroupId) {
        UserDto user = userReader.getUser(userProvider.get().getId());
        if (user.getTutorId() == null) throw new BusinessLogicException("Вы не являетесь преподавателем");
        return studyGroupReader.getCoursesForTutor(studyGroupId, user.getTutorId());
    }

    @PostMapping("{studyGroupId}/courses/search")
    public PageResponse<CourseWithTutorsDto> searchTutors(@PathVariable StudyGroupId studyGroupId, @RequestBody PageQuery pageQuery) {
        return studyGroupReader.searchCourses(studyGroupId, pageQuery);
    }

    @PostMapping("{studyGroupId}/courses/{courseId}")
    public CourseWithTutorsDto addCourse(@PathVariable StudyGroupId studyGroupId, @PathVariable CourseId courseId) {
        return studyGroupService.addCourse(studyGroupId, courseId);
    }

    @DeleteMapping("{studyGroupId}/courses/{courseId}")
    public void removeCourse(@PathVariable StudyGroupId studyGroupId, @PathVariable CourseId courseId) {
        studyGroupService.removeCourse(studyGroupId, courseId);
    }

    @GetMapping("{studyGroupId}/courses/{courseId}/tutors/{tutorId}")
    public TutorInCourseDto getTutor(@PathVariable StudyGroupId studyGroupId,
                                     @PathVariable CourseId courseId,
                                     @PathVariable TutorId tutorId) {
        return Optional.ofNullable(studyGroupReader.getTutor(new TutorInCourseId(studyGroupId, courseId, tutorId)))
                .orElseThrow(() -> new TutorNotFoundException(tutorId));
    }

    @GetMapping("{studyGroupId}/courses/{courseId}/tutors")
    public List<TutorInCourseDto> getTutors(@PathVariable StudyGroupId studyGroupId, @PathVariable CourseId courseId) {
        return studyGroupReader.getTutors(new CourseWithTutorsId(studyGroupId, courseId));
    }

    @PostMapping("{studyGroupId}/courses/{courseId}/tutors/search")
    public PageResponse<TutorInCourseDto> getTutors(@PathVariable StudyGroupId studyGroupId, @PathVariable CourseId courseId, @RequestBody PageQuery pageQuery) {
        return studyGroupReader.searchTutors(new CourseWithTutorsId(studyGroupId, courseId), pageQuery);
    }

    @PostMapping("{studyGroupId}/courses/{courseId}/tutors/{tutorId}")
    public TutorInCourseDto addTutor(@PathVariable StudyGroupId studyGroupId,
                                     @PathVariable CourseId courseId,
                                     @PathVariable TutorId tutorId) {
        return studyGroupService.addTutor(studyGroupId, courseId, tutorId);
    }

    @DeleteMapping("{studyGroupId}/courses/{courseId}/tutors/{tutorId}")
    public void removeTutor(@PathVariable StudyGroupId studyGroupId,
                            @PathVariable CourseId courseId,
                            @PathVariable TutorId tutorId) {
        studyGroupService.removeTutor(studyGroupId, courseId, tutorId);
    }
}
