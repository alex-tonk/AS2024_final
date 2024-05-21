package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Stand;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface StandRepository extends JpaRepository<Stand, StandId>, QuerydslPredicateExecutor<Stand> {
    Boolean existsByEndpointsName(String endpointName);
}
