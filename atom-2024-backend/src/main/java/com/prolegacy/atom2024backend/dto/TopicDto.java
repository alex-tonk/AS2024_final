package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.TopicId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TopicDto {
    private TopicId id;

    private String code;
    private String title;
    private String description;
}
