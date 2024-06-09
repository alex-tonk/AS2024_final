package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.ModuleDto;
import com.prolegacy.atom2024backend.entities.ids.ModuleId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private ModuleId id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String name;

    public Module(ModuleDto moduleDto) {
        update(moduleDto);
    }

    public void update(ModuleDto moduleDto) {
        name = moduleDto.getName();
    }
}
