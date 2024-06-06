package com.prolegacy.atom2024backend.survey.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;

public class SurveyNotFoundException extends BusinessLogicException {
    public SurveyNotFoundException() {
        super("Тестирование не найдено");
    }
}
