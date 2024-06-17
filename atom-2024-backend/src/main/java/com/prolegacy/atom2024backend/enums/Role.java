package com.prolegacy.atom2024backend.enums;

import lombok.AccessLevel;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Role {
    ADMIN("ROLE_admin", "Администратор"),
    STUDENT("ROLE_student", "Обучающийся"),
    TUTOR("ROLE_tutor", "Наставник");

    private final String roleName;
    private final String localization;
    @Getter(AccessLevel.NONE)
    private static final Map<String, Role> rolesByName;

    static {
        rolesByName = Arrays.stream(Role.values()).collect(Collectors.toMap(Role::getRoleName, Function.identity()));
    }

    Role(String roleName, String localization) {
        this.roleName = roleName;
        this.localization = localization;
    }

    @Nullable
    public static Role getRoleByName(String roleName) {
        return rolesByName.get(roleName);
    }
}
