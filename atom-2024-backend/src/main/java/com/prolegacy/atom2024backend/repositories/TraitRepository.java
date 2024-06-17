package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Trait;
import com.prolegacy.atom2024backend.entities.ids.TraitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraitRepository extends JpaRepository<Trait, TraitId> {
}
