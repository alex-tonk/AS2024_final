package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Lesson;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, LessonId> {
}
