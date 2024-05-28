package com.prolegacy.atom2024backend.entities.ids;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CourseWithTutorsId implements Serializable {
    StudyGroupId studyGroupId;
    CourseId courseId;
}
