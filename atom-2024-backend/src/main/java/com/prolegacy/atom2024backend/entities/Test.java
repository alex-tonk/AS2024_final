package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TestId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
public class Test {
    @EmbeddedId
    private TestId id;

    @ManyToOne
    @MapsId("courseId")
    private Course course;
}
