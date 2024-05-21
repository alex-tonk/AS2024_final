package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.dto.TestDto;
import com.prolegacy.atom2024backend.entities.QProduct;
import com.prolegacy.atom2024backend.entities.QStandEndpoint;
import com.prolegacy.atom2024backend.entities.QTest;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import com.prolegacy.atom2024backend.entities.ids.TestId;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class TestReader {
    private static final QTest test = QTest.test;
    private static final QStandEndpoint standEndpoint = QStandEndpoint.standEndpoint;
    private static final QProduct product = QProduct.product;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserProvider userProvider;

    @Value("${auth.admin-role:#{null}}")
    private String adminRoleName;

    public List<TestDto> getAllTests() {
        return baseQuery().fetch();
    }

    public List<TestDto> getGroupTests(TestGroupId groupId) {
        return baseQuery().where(test.testGroup.id.eq(groupId)).fetch();
    }

    public TestDto getGroupTest(TestGroupId groupId, TestId id) {
        return baseQuery()
                .where(test.testGroup.id.eq(groupId))
                .where(test.id.eq(id))
                .fetchFirst();
    }

    private JPAQuery<TestDto> baseQuery() {
        var executionSeconds =
                Expressions.cases()
                        .when(test.executionEndDate.isNotNull().and(test.executionStartDate.isNotNull()))
                        .then(
                                Expressions.numberTemplate(
                                        Long.class,
                                        "function('getDifferenceSeconds', {0}, {1})",
                                        test.executionEndDate,
                                        test.executionStartDate
                                )
                        ).otherwise(Expressions.nullExpression());

        var query = queryFactory
                .from(test)
                .leftJoin(standEndpoint).on(standEndpoint.id.eq(test.standEndpoint.id))
                .leftJoin(product).on(test.product.id.eq(product.id))
                .orderBy(test.id.desc())
                .selectDto(
                        TestDto.class,
                        executionSeconds.as("executionSeconds")
                );

        var user = userProvider.get();
        String adminRoleWithPrefix = Optional.ofNullable(adminRoleName).map(adminRole -> "ROLE_" + adminRole)
                .orElseThrow(() -> new BusinessLogicException("Не найдена роль администратора"));
        if (user.getRoles()
                .stream()
                .filter(r -> adminRoleWithPrefix.equals(r.getName()))
                .findFirst()
                .isEmpty()) {
            query = query.where(test.standEndpoint.id.in(user.getAvailableEndpointsId()));
        }
        return query;
    }
}
