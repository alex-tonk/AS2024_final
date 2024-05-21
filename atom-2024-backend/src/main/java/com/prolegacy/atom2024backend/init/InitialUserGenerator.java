package com.prolegacy.atom2024backend.init;

import com.prolegacy.atom2024backend.common.auth.entities.Role;
import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.repositories.RoleRepository;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 100)
public class InitialUserGenerator implements ApplicationRunner {

    @Value("${auth.admin-role:#{null}}")
    private String adminRoleName;
    @Value("${default-user.email}")
    private String email;
    @Value("${default-user.password}")
    private String password;
    @Value("${default-user.firstname}")
    private String firstname;
    @Value("${default-user.lastname}")
    private String lastname;
    @Value("${default-user.surname}")
    private String surname;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var user = userRepository.findByEmail("animegamer666@gmail.com");
        if (user.isPresent()) {
            return;
        }

        Role role = roleRepository.findByName("ROLE_" + adminRoleName)
                .orElseThrow(() -> new BusinessLogicException("Отсутствует роль администратора"));

        userRepository.save(
                new User(
                        this.email,
                        passwordEncoder.encode(Base64.getEncoder().encodeToString(DigestUtils.sha256(this.password))),
                        this.firstname,
                        this.lastname,
                        this.surname,
                        null,
                        Arrays.asList(role)
                )
        );
    }
}
