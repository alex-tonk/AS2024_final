package com.prolegacy.atom2024backend.common.auth.providers;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.exceptions.UserNotFoundException;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Provider;

@Component
public class UserProvider implements Provider<User> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User get() {
        return userRepository.findActualUserByEmail(
                ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getUsername()
        ).orElseThrow(UserNotFoundException::new);
    }
}
