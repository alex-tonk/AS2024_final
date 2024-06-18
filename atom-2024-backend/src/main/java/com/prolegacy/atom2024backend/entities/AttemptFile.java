package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.AttemptFileDto;
import com.prolegacy.atom2024backend.entities.ids.AttemptFileId;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AttemptFile {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private AttemptFileId id;

    @ManyToOne
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    private FileId fileId;
    @Column(columnDefinition = "text")
    private String comment;

    public AttemptFile(Attempt attempt, AttemptFileDto dto) {
        this.attempt = attempt;
        if (dto.getFileId() == null) {
            throw new BusinessLogicException("Отсутствует файл");
        }

        this.fileId = dto.getFileId();
        this.comment = dto.getComment();
    }
}
