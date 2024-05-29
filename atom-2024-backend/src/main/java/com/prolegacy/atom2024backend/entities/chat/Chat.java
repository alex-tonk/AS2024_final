package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private ChatId id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    @ManyToMany
    private Set<User> members = new HashSet<>();

    public Chat(ChatDto chatDto, List<User> members) {
        this.name = chatDto.getName();
        this.members.addAll(members);
    }

    public User addMember(User user) {
        members.add(user);
        return user;
    }

    public Message addMessage(User author, MessageDto messageDto) {
        User user = members.stream().filter(u -> u.getId().equals(author.getId())).findFirst()
                .orElseThrow(() -> new BusinessLogicException("Автор сообщения не состоит в чате"));
        Message message = new Message(this, user, messageDto);
        messages.add(message);
        return message;
    }
}
