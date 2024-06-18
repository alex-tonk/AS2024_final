package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Supplement;
import com.prolegacy.atom2024backend.entities.ids.SupplementId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplementRepository extends JpaRepository<Supplement, SupplementId> {
}
