package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class StandNotFoundException extends BusinessLogicException {
    public StandNotFoundException() {
        super("Стенд не найден");
    }
}
