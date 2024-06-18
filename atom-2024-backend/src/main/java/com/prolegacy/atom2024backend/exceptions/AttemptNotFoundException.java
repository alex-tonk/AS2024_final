package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class AttemptNotFoundException extends BusinessLogicException {
    public AttemptNotFoundException() {
        super("Попытка прохождения задания не найдена");
    }
}
