package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.CourseDto;
import com.prolegacy.atom2024backend.entities.ids.CourseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private CourseId id;

    String name;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();

    public Course(CourseDto dto) {
        update(dto);
    }

    public void update(CourseDto dto) {
        name = dto.getName();
    }
}
