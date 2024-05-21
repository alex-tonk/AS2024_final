package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class ProductNotFoundException extends BusinessLogicException {
    public ProductNotFoundException() {
        super("ДСЕ не найдена");
    }
}
