package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.ids.chat.MessageId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private MessageId id;

    @ManyToOne
    private Chat chat;

    @Column(length = 4000)
    private String content;

    private Instant createdDate = Instant.now();

    @ManyToOne
    private User author;
    @ManyToMany
    private List<User> readBy = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    public Message(Chat chat, User author, MessageDto messageDto) {
        this.chat = chat;
        this.author = author;
        this.content = messageDto.getContent();
        if (messageDto.getAttachments() == null) return;
        messageDto.getAttachments().stream().map(a -> new Attachment(this, a)).forEach(attachments::add);
    }
}
