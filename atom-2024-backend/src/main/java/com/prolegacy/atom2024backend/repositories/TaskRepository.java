package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Task;
import com.prolegacy.atom2024backend.entities.ids.TaskId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, TaskId> {
}
