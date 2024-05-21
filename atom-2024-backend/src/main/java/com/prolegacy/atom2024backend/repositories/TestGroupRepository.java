package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.TestGroup;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestGroupRepository extends JpaRepository<TestGroup, TestGroupId> {

    List<TestGroup> findAllByEndDateIsNull();
}
