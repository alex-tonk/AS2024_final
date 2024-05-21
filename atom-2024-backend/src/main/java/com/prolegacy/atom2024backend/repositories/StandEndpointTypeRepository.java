package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.StandEndpointType;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandEndpointTypeRepository extends JpaRepository<StandEndpointType, StandEndpointTypeId> {
}
