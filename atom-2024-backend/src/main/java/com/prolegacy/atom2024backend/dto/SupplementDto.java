package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.SupplementId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupplementDto {
    private SupplementId supplementId;
    private String title;
    private FileId fileId;
}
