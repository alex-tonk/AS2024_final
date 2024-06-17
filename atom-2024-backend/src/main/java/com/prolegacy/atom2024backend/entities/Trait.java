package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TraitId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class Trait {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TraitId id;

    @Column(columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String description;

    public Trait(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
