package com.prolegacy.atom2024backend.init;

import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.Role;
import com.prolegacy.atom2024backend.common.auth.repositories.RoleRepository;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.auth.services.UserService;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 100)
public class InitialUserGenerator implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var user = userRepository.findByEmail("animegamer666@gmail.com");
        if (user.isPresent()) {
            return;
        }

        Role role = roleRepository.findByName(com.prolegacy.atom2024backend.enums.Role.ADMIN.getRoleName())
                .orElseThrow(() -> new BusinessLogicException("Отсутствует роль администратора"));

        // TODO: убрать нахер
        Role studentRole = roleRepository.findByName(com.prolegacy.atom2024backend.enums.Role.STUDENT.getRoleName())
                .orElseThrow(() -> new BusinessLogicException("Отсутствует роль обучающегося"));
        Role tutorRole = roleRepository.findByName(com.prolegacy.atom2024backend.enums.Role.TUTOR.getRoleName())
                .orElseThrow(() -> new BusinessLogicException("Отсутствует роль наставника"));

        userService.createUser(UserDto.builder()
                .email("admin@admin.com")
                .password(Base64.getEncoder().encodeToString(DigestUtils.sha256("password")))
                .firstname("Админ")
                .lastname("Админов")
                .surname("Админович")
                .roles(new HashSet<>(List.of(new RoleDto(role.getId(), null, null))))
                .build()
        );

        userService.createUser(UserDto.builder()
                .email("student@student.com")
                .password(Base64.getEncoder().encodeToString(DigestUtils.sha256("password")))
                .firstname("О")
                .lastname("Обучающийся")
                .surname("О")
                .roles(new HashSet<>(List.of(new RoleDto(studentRole.getId(), null, null))))
                .build()
        );

        userService.createUser(UserDto.builder()
                .email("tutor@tutor.com")
                .password(Base64.getEncoder().encodeToString(DigestUtils.sha256("password")))
                .firstname("Н")
                .lastname("Наставник")
                .surname("Н")
                .roles(new HashSet<>(List.of(new RoleDto(tutorRole.getId(), null, null))))
                .build()
        );
    }
}
