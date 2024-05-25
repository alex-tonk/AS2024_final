package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Course;
import com.prolegacy.atom2024backend.entities.ids.CourseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, CourseId> {
}
