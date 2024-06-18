package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.entities.ids.SupplementId;
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
public class Supplement {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private SupplementId id;

    @Column(columnDefinition = "text")
    private String title;
    private FileId fileId;

    public Supplement(String title, FileId fileId) {
        this.title = title;
        this.fileId = fileId;
    }
}
