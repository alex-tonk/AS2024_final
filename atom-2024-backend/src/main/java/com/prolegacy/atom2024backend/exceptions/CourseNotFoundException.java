package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.CourseId;

public class CourseNotFoundException extends BusinessLogicException {
    public CourseNotFoundException(CourseId courseId) {
        super("Курс [id=%s] не найден".formatted(courseId));
    }
}
