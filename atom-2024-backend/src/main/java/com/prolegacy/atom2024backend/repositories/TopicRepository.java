package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Topic;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, TopicId> {
}
