package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class StandEndpointTypeNotFoundException extends BusinessLogicException {
    public StandEndpointTypeNotFoundException() {
        super("Некорректная настройка типа испытания. Обратитесь к администратору.");
    }
}
