package com.prolegacy.atom2024backend.dto.chat;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.entities.ids.chat.MessageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MessageDto {
    MessageId id;
    String content;
    Instant createdDate;
    UserDto author;
    List<UserDto> readBy;
    List<AttachmentDto> attachments;
}
