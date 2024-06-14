package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.EssayId;
import com.prolegacy.atom2024backend.entities.ids.FileId;
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
public class Essay {

    @Id
    @GeneratedValue(generator = "typed-sequence")
    private EssayId id;

    @Column(columnDefinition = "text")
    private String description;
    private FileId fileId;
}
