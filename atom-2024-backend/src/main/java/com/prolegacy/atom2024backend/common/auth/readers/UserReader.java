package com.prolegacy.atom2024backend.common.auth.readers;

import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.QRole;
import com.prolegacy.atom2024backend.common.auth.entities.QUser;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.query.lazy.PageHelper;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.common.query.query.DtoProjections;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(readOnly = true)
public class UserReader {
    private static final QUser user = QUser.user;
    private static final QUser dummyUser = new QUser("dummyUser");
    private static final QRole role = QRole.role;
    private static final QRole userRole = new QRole("userRole");

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private PageHelper pageHelper;

    @Nullable
    public UserDto getUserByEmail(String email) {
        return getUserByEmail(email, false);
    }

    public UserDto getUserByEmail(String email, boolean joinRoles) {
        UserDto userDto = baseQuery()
                .where(user.email.eq(email))
                .fetchFirst();
        if (joinRoles) {
            setRoles(userDto);
        }
        return userDto;
    }

    public UserDto getUser(UserId userId) {
        return getUser(userId, false);
    }

    public UserDto getUser(UserId userId, boolean joinRoles) {
        UserDto userDto = baseQuery()
                .where(user.id.eq(userId))
                .fetchFirst();
        if (joinRoles) {
            setRoles(userDto);
        }

        return userDto;
    }

    public List<UserDto> getUsers() {
        return getUsers(false);
    }

    public List<UserDto> getUsers(boolean joinRoles) {
        List<UserDto> userDtoList = baseQuery().orderBy(user.id.asc()).fetch();
        if (joinRoles) {
            setRoles(userDtoList);
        }
        return userDtoList;
    }

    public PageResponse<UserDto> searchUsers(PageQuery pageQuery) {
        return pageHelper.paginate(baseQuery(), pageQuery);
    }

    private void setRoles(UserDto userDto) {
        if (userDto == null) return;
        userDto.setRoles(
                queryFactory
                        .from(user)
                        .innerJoin(user.roles, userRole)
                        .innerJoin(role).on(userRole.id.eq(role.id))
                        .where(user.id.eq(userDto.getId()))
                        .selectDto(RoleDto.class, role)
                        .fetch()
        );
    }

    private void setRoles(List<UserDto> userDtoList) {
        JPAQuery<?> rolesQuery = queryFactory
                .from(user)
                .innerJoin(user.roles, userRole)
                .innerJoin(role).on(userRole.id.eq(role.id));
        Map<UserId, List<RoleDto>> rolesByUser = rolesQuery.transform(
                GroupBy.groupBy(user.id)
                        .as(GroupBy.list(DtoProjections.constructDto(rolesQuery, RoleDto.class, role)))
        );
        userDtoList.forEach(
                user -> user.setRoles(rolesByUser.getOrDefault(user.getId(), new ArrayList<>()))
        );
    }

    private JPAQuery<UserDto> baseQuery() {
        StringExpression shortName = user.firstname.concat(" ")
                .concat(
                        Expressions.cases()
                                .when(user.surname.isNotNull())
                                .then(user.surname.substring(0, 1).concat(" ").concat(user.lastname.substring(0, 1)))
                                .otherwise(user.lastname.substring(0, 1))
                );

        StringExpression fullName = user.firstname.concat(" ")
                .concat(
                        Expressions.cases()
                                .when(user.surname.isNotNull())
                                .then(user.surname.concat(" ").concat(user.lastname))
                                .otherwise(user.lastname)
                );

        JPAQuery<String> rolesAsString = queryFactory
                .from(dummyUser)
                .innerJoin(dummyUser.roles, userRole)
                .innerJoin(role).on(userRole.id.eq(role.id))
                .where(dummyUser.id.eq(user.id))
                .select(Expressions.stringTemplate("function('stringAgg', {0}, ', ')", role.localeName).stringValue());


        return queryFactory
                .from(user)
                .selectDto(
                        UserDto.class,
                        Expressions.as(Expressions.nullExpression(String.class), "password"),
                        shortName.as("shortName"),
                        fullName.as("fullName"),
                        Expressions.as(rolesAsString, "rolesAsString")
                );
    }
}
