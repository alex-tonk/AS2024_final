package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Feature;
import com.prolegacy.atom2024backend.entities.ids.FeatureId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, FeatureId> {
}
