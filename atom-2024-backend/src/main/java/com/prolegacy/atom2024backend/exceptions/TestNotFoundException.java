package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class TestNotFoundException extends BusinessLogicException {
    public TestNotFoundException() {
        super("Испытание не найдено");
    }
}
