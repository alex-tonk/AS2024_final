package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TaskId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TaskId id;

    @Column(unique = true, columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String content;
    @Column(columnDefinition = "numeric(19, 6)")
    private BigDecimal difficulty;
    private Integer time;
}
