package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.chat.Chat;
import com.prolegacy.atom2024backend.entities.chat.Message;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import com.prolegacy.atom2024backend.exceptions.ChatNotFoundException;
import com.prolegacy.atom2024backend.readers.ChatReader;
import com.prolegacy.atom2024backend.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatReader chatReader;
    @Autowired
    private UserProvider userProvider;

    public ChatDto createChat(ChatDto chatDto) {
        List<User> members = userRepository.findAllById(
                Optional.ofNullable(chatDto.getMembers()).orElseGet(ArrayList::new)
                        .stream()
                        .map(UserDto::getId)
                        .toList()
        );

        Chat chat = chatRepository.save(new Chat(chatDto, members));
        chat.addMember(userProvider.get());

        return chatReader.getChat(chat.getId());
    }

    public MessageDto addMessage(ChatId chatId, MessageDto messageDto) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
        Message message = chat.addMessage(userProvider.get(), messageDto);
        return chatReader.getMessage(chatId, message.getId());
    }
}
