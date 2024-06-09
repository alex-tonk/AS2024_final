package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.dto.CourseDto;
import com.prolegacy.atom2024backend.dto.ModuleDto;
import com.prolegacy.atom2024backend.entities.ids.CourseId;
import com.prolegacy.atom2024backend.exceptions.CourseNotFoundException;
import com.prolegacy.atom2024backend.readers.CourseReader;
import com.prolegacy.atom2024backend.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("courses")
@TypescriptEndpoint
public class CourseController {
    @Autowired
    private CourseReader courseReader;
    @Autowired
    private CourseService courseService;

    @GetMapping("{courseId}")
    public CourseDto getCourse(@PathVariable CourseId courseId) {
        return Optional.ofNullable(courseReader.getCourse(courseId))
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @GetMapping
    public List<CourseDto> getCourses() {
        return courseReader.getCourses();
    }

    @PostMapping("search")
    public PageResponse<CourseDto> searchCourses(@RequestBody PageQuery pageQuery) {
        return courseReader.searchCourses(pageQuery);
    }

    @PostMapping
    public CourseDto createCourse(@RequestBody CourseDto courseDto) {
        return courseService.createCourse(courseDto);
    }

    @PutMapping("{courseId}")
    public CourseDto updateCourse(@PathVariable CourseId courseId, @RequestBody CourseDto courseDto) {
        return courseService.updateCourse(courseId, courseDto);
    }

    @PostMapping("{courseId}/module")
    public CourseDto addModule(@PathVariable CourseId courseId, @RequestBody ModuleDto moduleDto) {
        return courseService.addModule(courseId, moduleDto);
    }
}
