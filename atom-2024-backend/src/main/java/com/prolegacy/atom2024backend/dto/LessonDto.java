package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.LessonId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LessonDto {
    private LessonId id;
    private String code;
    private String title;
    private String content;
    private String author;
}
