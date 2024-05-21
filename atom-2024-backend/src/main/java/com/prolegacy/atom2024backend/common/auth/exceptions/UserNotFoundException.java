package com.prolegacy.atom2024backend.common.auth.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class UserNotFoundException extends BusinessLogicException {
    public UserNotFoundException() {
        super("Пользователь не найден");
    }

    public UserNotFoundException(String email) {
        super(email == null ? "Пользователь не найден" : "Пользователь с email-адресом %s не найден".formatted(email));
    }
}
