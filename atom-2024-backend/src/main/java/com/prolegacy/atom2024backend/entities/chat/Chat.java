package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.enums.ChatType;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    public static final String FAVORITE = "Избранное";
    public static final String GROUP_CHAT = "Чат группы";

    @Id
    @GeneratedValue(generator = "typed-sequence")
    private ChatId id;

    private String name;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    @ManyToMany
    private Set<User> members = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatType type = ChatType.PUBLIC;

    public Chat(ChatDto chatDto, List<User> members) {
        this.name = chatDto.getName();
        this.type = chatDto.getType();
        this.members.addAll(members);
    }

    public Chat(User user) {
        this.name = "[%s] %s".formatted(FAVORITE, user.getShortName());
        this.members.add(user);
        this.type = ChatType.FAVORITE;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeUser(User user) {
        members.removeIf(u -> Objects.equals(user.getId(), u.getId()));
    }

    public void addMessage(User author, MessageDto messageDto) {
        User user = members.stream().filter(u -> Objects.equals(u.getId(), author.getId())).findFirst()
                .orElseThrow(() -> new BusinessLogicException("Автор сообщения не состоит в чате"));
        Message message = new Message(this, user, messageDto);
        messages.add(message);
    }
}
