package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Student;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, StudentId> {
}
