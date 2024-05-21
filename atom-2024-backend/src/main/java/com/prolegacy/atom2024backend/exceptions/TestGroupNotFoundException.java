package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class TestGroupNotFoundException extends BusinessLogicException {
    public TestGroupNotFoundException() {
        super("Испытание не найдено");
    }
}
