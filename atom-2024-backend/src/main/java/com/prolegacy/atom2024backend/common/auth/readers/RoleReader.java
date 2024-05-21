package com.prolegacy.atom2024backend.common.auth.readers;

import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.entities.QRole;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class RoleReader {
    private static final QRole role = QRole.role;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<RoleDto> getRoles() {
        return this.baseQuery().fetch();
    }

    private JPAQuery<RoleDto> baseQuery() {
        return queryFactory
                .from(role)
                .selectDto(RoleDto.class);
    }
}
