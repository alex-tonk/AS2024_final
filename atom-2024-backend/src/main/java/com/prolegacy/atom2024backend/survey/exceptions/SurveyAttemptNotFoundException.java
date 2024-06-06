package com.prolegacy.atom2024backend.survey.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class SurveyAttemptNotFoundException extends BusinessLogicException {
    public SurveyAttemptNotFoundException() {
        super("Тестирование не найдено");
    }
}
