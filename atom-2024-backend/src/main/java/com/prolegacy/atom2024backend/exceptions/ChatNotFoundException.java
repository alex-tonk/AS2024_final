package com.prolegacy.atom2024backend.exceptions;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;

public class ChatNotFoundException extends BusinessLogicException {
    public ChatNotFoundException(ChatId chatId) {
        super("Чат [id=%s] не найден".formatted(chatId));
    }
}
