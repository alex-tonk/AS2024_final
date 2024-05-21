package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.StandEndpointTypeDto;
import com.prolegacy.atom2024backend.entities.QStandEndpoint;
import com.prolegacy.atom2024backend.entities.QStandEndpointType;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointTypeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StandEndpointTypeReader {
    private static final QStandEndpointType standEndpointType = QStandEndpointType.standEndpointType;
    private static final QStandEndpoint standEndpoint = QStandEndpoint.standEndpoint;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<StandEndpointTypeDto> getEndpointTypes() {
        return baseQuery().fetch();
    }

    public StandEndpointTypeDto getEndpointType(StandEndpointTypeId id) {
        return baseQuery().where(standEndpointType.id.eq(id)).fetchFirst();
    }

    private JPAQuery<StandEndpointTypeDto> baseQuery() {
        return queryFactory
                .from(standEndpointType)
                .leftJoin(standEndpoint).on(standEndpoint.standEndpointType.id.eq(standEndpointType.id))
                .selectDto(
                        StandEndpointTypeDto.class,
                        standEndpoint.name.as("name")
                )
                .orderBy(standEndpointType.id.desc());
    }
}