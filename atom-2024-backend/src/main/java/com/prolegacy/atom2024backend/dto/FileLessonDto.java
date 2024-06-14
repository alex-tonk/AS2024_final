package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.FileId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FileLessonDto {
    private String name;
    private FileId fileId;
}
