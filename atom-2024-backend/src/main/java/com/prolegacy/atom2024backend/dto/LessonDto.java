package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.enums.LessonType;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.entities.ids.LessonInStudyGroupId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LessonDto {
    private LessonId id;
    private LessonInStudyGroupId lessonInStudyGroupId;
    private LessonType type;
    private String name;
    private Long orderNumber;

    private Instant beginDate;
    private Instant endDate;
    private String description;
}
