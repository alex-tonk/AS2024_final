package com.prolegacy.atom2024backend.dto.chat;

import com.prolegacy.atom2024backend.entities.ids.chat.AttachmentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttachmentDto {
    AttachmentId id;
    MessageDto message;
    URI uri;
}
