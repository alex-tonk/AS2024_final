package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.StandEndpoint;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandEndpointRepository extends JpaRepository<StandEndpoint, StandEndpointId> {
}
