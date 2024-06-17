package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.entities.Attempt;
import com.prolegacy.atom2024backend.entities.Lesson;
import com.prolegacy.atom2024backend.entities.Task;
import com.prolegacy.atom2024backend.entities.Topic;
import com.prolegacy.atom2024backend.entities.ids.AttemptId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, AttemptId> {
    Optional<Attempt> getByTopicAndLessonAndTaskAndUserAndIsLastAttemptTrue(Topic topic, Lesson lesson, Task task, User user);
}
