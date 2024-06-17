package com.prolegacy.atom2024backend.enums;

import java.math.BigDecimal;

public enum Mark {
    EXCELLENT(BigDecimal.valueOf(5), "Отлично"),
    GOOD(BigDecimal.valueOf(4), "Хорошо"),
    MEDIOCRE(BigDecimal.valueOf(3), "Удовлетворительно"),
    FAILED(BigDecimal.valueOf(2), "Неудовлетворительно");


    public final BigDecimal value;
    public final String description;

    Mark(BigDecimal value, String description) {
        this.value = value;
        this.description = description;
    }
}
