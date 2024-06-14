package com.prolegacy.atom2024backend.entities.ids;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class LessonInStudyGroupId implements Serializable {
    private StudyGroupId studyGroupId;
    private LessonId lessonId;
}
