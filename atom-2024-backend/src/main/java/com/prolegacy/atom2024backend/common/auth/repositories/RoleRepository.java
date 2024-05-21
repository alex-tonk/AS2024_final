package com.prolegacy.atom2024backend.common.auth.repositories;

import com.prolegacy.atom2024backend.common.auth.entities.Role;
import com.prolegacy.atom2024backend.common.auth.entities.id.RoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, RoleId> {
    void deleteByNameIn(Set<String> roleNames);

    Optional<Role> findByName(String roleName);
}
