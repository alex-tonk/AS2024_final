package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.*;
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
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private ChatId id;

    private String name;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    @ManyToMany
    private Set<User> members = new HashSet<>();

    @OneToOne
    private StudyGroup studyGroup;

    public Chat(ChatDto chatDto, List<User> members) {
        this.name = chatDto.getName();
        this.members.addAll(members);
    }

    public Chat(ChatDto chatDto, StudyGroup studyGroup) {
        this.name = chatDto.getName();
        this.members.addAll(studyGroup.getStudents().stream().map(StudentInGroup::getStudent).map(Student::getUser).toList());
        this.members.addAll(studyGroup.getCourses().stream().flatMap(c -> c.getTutors().stream()).map(TutorInCourse::getTutor).map(Tutor::getUser).toList());
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeUser(User user) {
        if (studyGroup != null && !user.getArchived()) {
            throw new BusinessLogicException("Из чата учебной группы можно удалять только архивных пользователей");
        }

        members.removeIf(u -> Objects.equals(user.getId(), u.getId()));
    }

    public void addMessage(User author, MessageDto messageDto) {
        User user = members.stream().filter(u -> Objects.equals(u.getId(), author.getId())).findFirst()
                .orElseThrow(() -> new BusinessLogicException("Автор сообщения не состоит в чате"));
        Message message = new Message(this, user, messageDto);
        messages.add(message);
    }
}
