package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.TutorId;

public class TutorNotFoundException extends BusinessLogicException {
    public TutorNotFoundException(TutorId tutorId) {
        super("Преподаватель [id=%s] не найден".formatted(tutorId));
    }
}
