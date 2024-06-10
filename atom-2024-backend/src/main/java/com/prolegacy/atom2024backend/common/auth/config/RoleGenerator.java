package com.prolegacy.atom2024backend.common.auth.config;


import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.readers.RoleReader;
import com.prolegacy.atom2024backend.common.auth.repositories.RoleRepository;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import com.prolegacy.atom2024backend.enums.Role;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR)
@Log4j2
public class RoleGenerator implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleReader roleReader;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        Map<String, RoleDto> hardcodedRoles = Arrays.stream(Role.values())
                .map(role -> new RoleDto(null, role.getRoleName(), role.getLocalization()))
                .collect(Collectors.toMap(RoleDto::getName, Function.identity()));
        Map<String, RoleDto> existingRoles = this.roleReader.getRoles()
                .stream()
                .collect(Collectors.toMap(RoleDto::getName, Function.identity()));
        Set<String> existingRoleNames = existingRoles.keySet();
        Set<String> hardcodedRoleNames = hardcodedRoles.keySet();
        Set<String> rolesToDelete = SetUtils.difference(existingRoleNames, hardcodedRoleNames);
        if (!rolesToDelete.isEmpty()) {
            roleRepository.deleteByNameIn(rolesToDelete);
        }
        roleRepository.saveAll(SetUtils.difference(hardcodedRoleNames, existingRoleNames)
                .stream()
                .map(hardcodedRoles::get)
                .map(roleDto -> new com.prolegacy.atom2024backend.common.auth.entities.Role(roleDto.getName(), roleDto.getLocaleName()))
                .collect(Collectors.toList()));
    }
}
