package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.StudyGroupId;

public class StudyGroupNotFoundException extends BusinessLogicException {
    public StudyGroupNotFoundException(StudyGroupId studyGroupId) {
        super("Учебная группа [id=%s] не найдена".formatted(studyGroupId));
    }
}
