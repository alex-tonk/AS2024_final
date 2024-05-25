package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.dto.CourseDto;
import com.prolegacy.atom2024backend.entities.Course;
import com.prolegacy.atom2024backend.entities.ids.CourseId;
import com.prolegacy.atom2024backend.exceptions.CourseNotFoundException;
import com.prolegacy.atom2024backend.readers.CourseReader;
import com.prolegacy.atom2024backend.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseReader courseReader;

    public CourseDto createCourse(CourseDto courseDto) {
        Course course = courseRepository.save(new Course(courseDto));
        return courseReader.getCourse(course.getId());
    }

    public CourseDto updateCourse(CourseId courseId, CourseDto courseDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        course.update(courseDto);
        return courseReader.getCourse(course.getId());
    }
}
