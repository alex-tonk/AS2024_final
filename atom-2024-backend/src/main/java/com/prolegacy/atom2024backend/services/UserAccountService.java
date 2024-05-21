package com.prolegacy.atom2024backend.services;

import com.google.common.base.Strings;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserAccountService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserReader userReader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProvider userProvider;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto updateUser(UserId userId, String password, UserDto userDto) {
        User user = userProvider.get();

        if (!Objects.equals(userId, user.getId())) {
            throw new BusinessLogicException("Нельзя изменять чужой аккаунт");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessLogicException("Неверный пароль");
        }

        String newPassword = null;
        if (userDto.getPassword() != null && !passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            if (Strings.isNullOrEmpty(userDto.getPassword())) {
                throw new BusinessLogicException("Новый пароль не может быть пустым");
            }

            newPassword = passwordEncoder.encode(userDto.getPassword());
        }

        user.update(userDto, newPassword);

        return userReader.getUser(userId, true);
    }
}
