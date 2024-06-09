package com.prolegacy.atom2024backend.dto.chat;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.entities.enums.ChatType;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ChatDto {
    ChatId id;
    String name;
    List<MessageDto> messages;
    List<UserDto> members;
    MessageDto lastMessage;
    ChatType type;
}
