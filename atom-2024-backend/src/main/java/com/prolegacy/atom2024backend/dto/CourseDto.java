package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.CourseId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CourseDto {
    CourseId id;
    String name;
    List<ModuleDto> modules;
}
