package com.prolegacy.atom2024backend.common.auth.repositories;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {

    default Optional<User> findActualUserByEmail(String email) {
        return this.findByEmailAndArchivedIsFalse(email);
    }

    default List<User> findActualUsersWithRoleName(String roleName) {
        return this.findByRolesNameAndArchivedIsFalse(roleName);
    }

    Optional<User> findByEmailAndArchivedIsFalse(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String username);

    List<User> findByRolesNameAndArchivedIsFalse(String roleName);
}
