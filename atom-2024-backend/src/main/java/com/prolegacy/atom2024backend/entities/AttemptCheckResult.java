package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.AttemptCheckResultDto;
import com.prolegacy.atom2024backend.entities.ids.AttemptCheckResultId;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class AttemptCheckResult {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private AttemptCheckResultId id;

    @Column(columnDefinition = "numeric(19, 6)")
    private BigDecimal x1;
    @Column(columnDefinition = "numeric(19, 6)")
    private BigDecimal y1;
    @Column(columnDefinition = "numeric(19, 6)")
    private BigDecimal x2;
    @Column(columnDefinition = "numeric(19, 6)")
    private BigDecimal y2;
    private FileId fileId;
    @Column(columnDefinition = "text")
    private String comment;

    @ManyToMany
    @JoinTable(
            name = "attempt_check_results_features",
            joinColumns = @JoinColumn(name = "attempt_check_result_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<Feature> features = new ArrayList<>();

    private Boolean isAutomatic;

    public AttemptCheckResult(
            FileId fileId,
            AttemptCheckResultDto dto,
            List<Feature> features
    ) {
        this.x1 = dto.getX1();
        this.y1 = dto.getY1();
        this.x2 = dto.getX2();
        this.y2 = dto.getY2();
        this.isAutomatic = dto.getIsAutomatic();
        this.comment = dto.getComment();
        this.features = features;
        this.fileId = fileId;
    }

    public AttemptCheckResult(FileId fileId, com.prolegacy.atom2024backend.dto.integration.AttemptCheckResultDto dto, List<Feature> features) {
        this.x1 = dto.getArea().getX1();
        this.y1 = dto.getArea().getY1();
        this.x2 = dto.getArea().getX2();
        this.y2 = dto.getArea().getY2();
        this.isAutomatic = true;
        this.features = features;
        this.fileId = fileId;
    }
}
