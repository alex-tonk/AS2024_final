package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.AttemptFileId;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttemptFileDto {
    private AttemptFileId id;
    private FileId fileId;
    private String comment;
    private String fileName;
}
