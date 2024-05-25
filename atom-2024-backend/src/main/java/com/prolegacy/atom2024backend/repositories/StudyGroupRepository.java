package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.StudyGroup;
import com.prolegacy.atom2024backend.entities.ids.StudyGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, StudyGroupId> {
}
