package com.prolegacy.atom2024backend.common.exceptions;

public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException() {
        super("Произошла ошибка на сервере");
    }

    public BusinessLogicException(String message) {
        super(message);
    }
}
