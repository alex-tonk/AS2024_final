package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.entities.QStand;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StandReader {
    private static final QStand stand = QStand.stand;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<StandDto> getStands() {
        return baseQuery().fetch();
    }

    public StandDto getStand(StandId id) {
        return baseQuery().where(stand.id.eq(id)).fetchFirst();
    }

    private JPAQuery<StandDto> baseQuery() {
        return queryFactory
                .from(stand)
                .selectDto(StandDto.class);
    }
}
