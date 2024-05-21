package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.entities.QStand;
import com.prolegacy.atom2024backend.entities.QStandEndpoint;
import com.prolegacy.atom2024backend.entities.QStandEndpointType;
import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class StandEndpointReader {
    private static final QStandEndpoint standEndpoint = QStandEndpoint.standEndpoint;
    private static final QStand stand = QStand.stand;
    private static final QStandEndpointType standEndpointType = QStandEndpointType.standEndpointType;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserProvider userProvider;

    @Value("${auth.admin-role:#{null}}")
    private String adminRoleName;

    public List<StandEndpointDto> getStandEndpoints(StandId standId) {
        return forStand(standId).fetch();
    }

    public List<StandEndpointDto> getAllStandEndpoints() {
        return baseQuery().fetch();
    }

    public List<StandEndpointDto> getVirtualEndpoints() {
        return baseQuery().where(standEndpoint.stand.computationType.eq(ComputationType.EMULATED)).fetch();
    }

    public StandEndpointDto getStandEndpoint(StandId standId, StandEndpointId id) {
        return forStand(standId).where(standEndpoint.id.eq(id)).fetchFirst();
    }

    private JPAQuery<StandEndpointDto> forStand(StandId standId) {
        return baseQuery()
                .where(standEndpoint.stand.id.eq(standId));
    }

    private JPAQuery<StandEndpointDto> baseQuery() {
        var query = queryFactory
                .from(standEndpoint)
                .leftJoin(stand).on(stand.id.eq(standEndpoint.stand.id))
                .leftJoin(standEndpointType).on(standEndpoint.standEndpointType.id.eq(standEndpointType.id))
                .selectDto(
                        StandEndpointDto.class,
                        standEndpoint.name.as("standEndpointType.name")
                );

        var user = userProvider.get();
        String adminRoleWithPrefix = Optional.ofNullable(adminRoleName).map(adminRole -> "ROLE_" + adminRole)
                .orElseThrow(() -> new BusinessLogicException("Не найдена роль администратора"));
        if (user.getRoles()
                .stream()
                .filter(r -> adminRoleWithPrefix.equals(r.getName()))
                .findFirst()
                .isEmpty()) {
            query = query.where(standEndpoint.id.in(user.getAvailableEndpointsId()));
        }
        return query.orderBy(standEndpoint.id.desc());
    }
}