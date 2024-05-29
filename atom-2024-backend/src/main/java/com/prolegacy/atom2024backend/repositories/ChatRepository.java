package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.chat.Chat;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, ChatId> {
}
