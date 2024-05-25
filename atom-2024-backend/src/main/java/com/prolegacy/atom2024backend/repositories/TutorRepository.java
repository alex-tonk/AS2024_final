package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Tutor;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, TutorId> {
}
