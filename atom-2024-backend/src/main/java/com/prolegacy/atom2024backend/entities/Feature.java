package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.FeatureId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Feature {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private FeatureId id;

    @Column(columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String name;
}
