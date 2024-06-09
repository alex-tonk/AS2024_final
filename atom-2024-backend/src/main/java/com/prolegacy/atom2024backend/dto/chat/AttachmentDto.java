package com.prolegacy.atom2024backend.dto.chat;

import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.chat.AttachmentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttachmentDto {
    AttachmentId id;
    MessageDto message;
    FileId fileId;
    String fileName;
}
