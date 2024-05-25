package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.dto.StudentDto;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import com.prolegacy.atom2024backend.exceptions.StudentNotFoundException;
import com.prolegacy.atom2024backend.readers.StudentReader;
import com.prolegacy.atom2024backend.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("students")
@TypescriptEndpoint
public class StudentController {
    @Autowired
    private StudentReader studentReader;
    @Autowired
    private StudentService studentService;

    @GetMapping("{studentId}")
    public StudentDto getStudent(@PathVariable StudentId studentId) {
        return Optional.ofNullable(studentReader.getStudent(studentId))
                .orElseThrow(() -> new StudentNotFoundException(studentId));
    }

    @GetMapping
    public List<StudentDto> getStudents() {
        return studentReader.getStudents();
    }

    @PostMapping("search")
    public PageResponse<StudentDto> searchStudents(@RequestBody PageQuery pageQuery) {
        return studentReader.searchStudents(pageQuery);
    }

    @PostMapping
    public StudentDto createStudent(@RequestBody StudentDto studentDto) {
        return studentService.createStudent(studentDto);
    }

    @PutMapping("{studentId}")
    public StudentDto updateStudent(@PathVariable StudentId studentId, @RequestBody StudentDto studentDto) {
        return studentService.updateStudent(studentId, studentDto);
    }
}
