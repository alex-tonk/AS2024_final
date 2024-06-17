package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class TopicNotFoundException extends BusinessLogicException {
    public TopicNotFoundException() {
        super("Данной темы не существует");
    }
}
