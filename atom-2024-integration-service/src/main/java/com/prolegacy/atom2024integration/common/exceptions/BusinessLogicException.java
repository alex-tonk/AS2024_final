package com.prolegacy.atom2024integration.common.exceptions;

public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException() {
        super("Произошла ошибка на сервере");
    }

    public BusinessLogicException(String message) {
        super(message);
    }
}
