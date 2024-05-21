package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.entities.QUser;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.query.query.DtoProjections;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.TestDto;
import com.prolegacy.atom2024backend.dto.TestGroupDto;
import com.prolegacy.atom2024backend.dto.enums.TestGroupFilterEnum;
import com.prolegacy.atom2024backend.entities.QProduct;
import com.prolegacy.atom2024backend.entities.QStandEndpoint;
import com.prolegacy.atom2024backend.entities.QTest;
import com.prolegacy.atom2024backend.entities.QTestGroup;
import com.prolegacy.atom2024backend.entities.enums.TestStatus;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class TestGroupReader {
    private static final QTestGroup testGroup = QTestGroup.testGroup;
    private static final QTest test = QTest.test;
    private static final QTest testGroupTest = new QTest("testTestGroup");
    private static final QUser user = new QUser("startUser");
    private static final QProduct product = new QProduct("product");
    private static final QStandEndpoint endpoint = QStandEndpoint.standEndpoint;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserProvider userProvider;

    @Value("${auth.admin-role:#{null}}")
    private String adminRoleName;

    public List<TestGroupDto> getTestGroups(TestGroupFilterEnum filterEnum) {
        List<TestGroupDto> testGroupDtos = baseQuery(filterEnum).fetch();
        setTests(testGroupDtos);
        return testGroupDtos;
    }

    public TestGroupDto getTestGroup(TestGroupId id) {
        TestGroupDto testGroupDto = baseQuery(TestGroupFilterEnum.ALL).where(testGroup.id.eq(id)).fetchFirst();
        if (testGroupDto != null)
            setTests(List.of(testGroupDto));
        return testGroupDto;
    }

    private JPAQuery<TestGroupDto> baseQuery(TestGroupFilterEnum filterEnum) {
        StringExpression shortName = user.firstname.concat(" ")
                .concat(
                        Expressions.cases()
                                .when(user.surname.isNotNull())
                                .then(user.surname.substring(0, 1).concat(" ").concat(user.lastname.substring(0, 1)))
                                .otherwise(user.lastname.substring(0, 1))
                );

        var executionSeconds =
                Expressions.cases()
                        .when(testGroup.endDate.isNotNull().and(testGroup.startDate.isNotNull()))
                        .then(
                                Expressions.numberTemplate(
                                        Long.class,
                                        "function('getDifferenceSeconds', {0}, {1})",
                                        testGroup.endDate,
                                        testGroup.startDate
                                )
                        ).otherwise(Expressions.nullExpression());

        var isBusy = queryFactory.from(test)
                .where(test.testGroup.id.eq(testGroup.id))
                .where(test.testStatus.in(List.of(TestStatus.EXECUTING, TestStatus.REGISTERED, TestStatus.UNREGISTERED)))
                .exists();
        var hasError = queryFactory.from(test)
                .where(test.testGroup.id.eq(testGroup.id))
                .where(test.testStatus.eq(TestStatus.ERROR))
                .exists();
        var isStopped = queryFactory.from(test)
                .where(test.testGroup.id.eq(testGroup.id))
                .where(test.testStatus.eq(TestStatus.CANCELLED))
                .exists();

        var query = queryFactory
                .from(testGroup)
                .leftJoin(user).on(testGroup.startUser.id.eq(user.id))
                .selectDto(
                        TestGroupDto.class,
                        shortName.as("startUser.shortName"),
                        executionSeconds.as("executionSeconds"),
                        Expressions.as(Expressions.nullExpression(String.class), "startUser.password"),
                        isBusy.as("isBusy"),
                        hasError.as("hasError"),
                        isStopped.as("isStopped")
                ).orderBy(testGroup.id.desc());

        if (filterEnum != null && filterEnum != TestGroupFilterEnum.ALL) {
            query = switch (filterEnum) {
                case IN_PROGRESS -> query.where(isBusy.and(isStopped.not()).and(hasError.not()));
                case STOPPED -> query.where(isStopped);
                case ERRORS -> query.where(hasError);
                case DONE -> query.where(isBusy.not().and(isStopped.not()).and(hasError.not()));
                default -> throw new IllegalStateException("Unexpected value: " + filterEnum);
            };
        }

        var user = userProvider.get();
        String adminRoleWithPrefix = Optional.ofNullable(adminRoleName).map(adminRole -> "ROLE_" + adminRole)
                .orElseThrow(() -> new BusinessLogicException("Не найдена роль администратора"));
        if (user.getRoles()
                .stream()
                .filter(r -> adminRoleWithPrefix.equals(r.getName()))
                .findFirst()
                .isEmpty()) {
            query = query.where(
                    (testGroup.tests.any().standEndpoint.id.notIn(user.getAvailableEndpointsId())).not()
            );
        }

        return query;
    }

    private void setTests(List<TestGroupDto> groupDtoList) {
        JPAQuery<?> testDtoJPAQuery = queryFactory
                .from(testGroup)
                .innerJoin(testGroup.tests, testGroupTest)
                .leftJoin(test).on(test.id.eq(testGroupTest.id))
                .leftJoin(product).on(product.id.eq(test.product.id))
                .leftJoin(endpoint).on(endpoint.id.eq(test.standEndpoint.id))
                .orderBy(test.id.desc());

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

        var testType = Expressions.cases()
                .when(endpoint.jsCode.isNotNull())
                .then("Моделирование")
                .otherwise("Стендовое испытание");

        Map<TestGroupId, List<TestDto>> testsByGroup = testDtoJPAQuery.transform(
                GroupBy.groupBy(testGroup.id)
                        .as(GroupBy.list(
                                DtoProjections.constructDto(
                                        testDtoJPAQuery,
                                        TestDto.class,
                                        test,
                                        executionSeconds.as("executionSeconds"),
                                        product.caption.as("productCaption"),
                                        endpoint.description.as("standEndpointDescription"),
                                        testType.as("testType")
                                )
                        ))
        );
        groupDtoList.forEach(
                testGroupDto -> testGroupDto.setTests(testsByGroup.getOrDefault(testGroupDto.getId(), new ArrayList<>()))
        );
    }
}
