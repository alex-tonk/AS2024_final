package com.prolegacy.atom2024backend.common.exceptions;

public class InsufficientPrivilegesException extends RuntimeException {
    public InsufficientPrivilegesException() {
        super("Недостаточно прав для выполнения запроса");
    }
}
