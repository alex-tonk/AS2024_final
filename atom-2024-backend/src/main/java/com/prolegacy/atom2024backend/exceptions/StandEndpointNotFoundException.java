package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class StandEndpointNotFoundException extends BusinessLogicException {
    public StandEndpointNotFoundException() {
        super("Некорректный тип испытания");
    }
}
