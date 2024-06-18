package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TaskId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TaskId id;

    @Column(columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String content;
    @Column(columnDefinition = "numeric(19, 6)")
    private BigDecimal difficulty;
    private Integer time;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_supplement",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "supplement_id")
    )
    private List<Supplement> supplements = new ArrayList<>();

    public Task(String code, String title, String content, BigDecimal difficulty, Integer time) {
        this.code = code;
        this.title = title;
        this.content = content;
        this.difficulty = difficulty;
        this.time = time;
    }
}
