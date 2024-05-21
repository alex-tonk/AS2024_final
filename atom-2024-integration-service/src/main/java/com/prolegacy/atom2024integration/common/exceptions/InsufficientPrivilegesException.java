package com.prolegacy.atom2024integration.common.exceptions;

public class InsufficientPrivilegesException extends RuntimeException {
    public InsufficientPrivilegesException() {
        super("Недостаточно прав для выполнения запроса");
    }
}
