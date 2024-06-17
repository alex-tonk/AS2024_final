package com.prolegacy.atom2024backend.enums;

public enum AttemptStatus {
    IN_PROGRESS("Взято в работу"),
    VALIDATION("Отправлено на проверку"),
    DONE("Проверено");

    public final String locale;

    AttemptStatus(String locale) {
        this.locale = locale;
    }
}
