package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.StudentId;

public class StudentNotFoundException extends BusinessLogicException {
    public StudentNotFoundException(StudentId studentId) {
        super("Ученик [id=%s] не найден".formatted(studentId));
    }
}
