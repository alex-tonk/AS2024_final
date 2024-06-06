package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Immutable
@Entity
@Subselect("""
        SELECT -1      as id,
               chat.id as chat_id,
               null    as user_id
        FROM "pro_legacy".chat
        union all
        SELECT -1   as id,
               null as chat_id,
               "user".id
        FROM "pro_legacy"."user"
        """)
public class AllChats {
    @Id
    private Long id;
    private ChatId chatId;
    private UserId userId;
}

