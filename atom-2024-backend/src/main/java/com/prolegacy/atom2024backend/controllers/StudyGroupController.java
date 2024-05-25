package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.TutorWithCourseDto;
import com.prolegacy.atom2024backend.entities.ids.*;
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

    @GetMapping("{studyGroupId}")
    public StudyGroupDto getStudyGroup(@PathVariable StudyGroupId studyGroupId) {
        return Optional.ofNullable(studyGroupReader.getStudyGroup(studyGroupId))
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
    }

    @GetMapping
    public List<StudyGroupDto> getStudyGroups() {
        return studyGroupReader.getStudyGroups();
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

    @GetMapping("{studyGroupId}/tutors/{tutorId}/{courseId}")
    public TutorWithCourseDto getTutor(@PathVariable StudyGroupId studyGroupId,
                                       @PathVariable TutorId tutorId,
                                       @PathVariable CourseId courseId) {
        return Optional.ofNullable(studyGroupReader.getTutor(new TutorWithCourseId(studyGroupId, tutorId, courseId)))
                .orElseThrow(() -> new TutorNotFoundException(tutorId));
    }

    @GetMapping("{studyGroupId}/tutors")
    public List<TutorWithCourseDto> getTutors(@PathVariable StudyGroupId studyGroupId) {
        return studyGroupReader.getTutors(studyGroupId);
    }

    @PostMapping("{studyGroupId}/tutors/search")
    public PageResponse<TutorWithCourseDto> searchTutors(@PathVariable StudyGroupId studyGroupId, @RequestBody PageQuery pageQuery) {
        return studyGroupReader.searchTutors(studyGroupId, pageQuery);
    }

    @PostMapping("{studyGroupId}/tutors/{tutorId}/{courseId}")
    public TutorWithCourseDto addTutor(@PathVariable StudyGroupId studyGroupId,
                                       @PathVariable TutorId tutorId,
                                       @PathVariable CourseId courseId) {
        return studyGroupService.addTutor(studyGroupId, tutorId, courseId);
    }

    @DeleteMapping("{studyGroupId}/tutors/{tutorId}/{courseId}")
    public void removeTutor(@PathVariable StudyGroupId studyGroupId,
                            @PathVariable TutorId tutorId,
                            @PathVariable CourseId courseId) {
        studyGroupService.removeTutor(studyGroupId, tutorId, courseId);
    }
}
