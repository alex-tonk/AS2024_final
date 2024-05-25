package com.prolegacy.atom2024backend.init;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 101)
public class TestDataGenerationService implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = Instancio.ofList(User.class)
                .size(10)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getRoles))
                .create();


        userRepository.saveAll(users);
    }
}
