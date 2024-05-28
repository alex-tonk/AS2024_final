package com.prolegacy.atom2024backend.entities.ids;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TestId implements Serializable {
    CourseId courseId;
    String name;
}
