package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import com.prolegacy.atom2024backend.readers.ChatReader;
import com.prolegacy.atom2024backend.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("chats")
@TypescriptEndpoint
public class ChatController {
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private ChatReader chatReader;
    @Autowired
    private ChatService chatService;

    @GetMapping
    public List<ChatDto> getChats() {
        return chatReader.getChats(userProvider.get().getId());
    }

    @GetMapping("{chatId}")
    public List<MessageDto> getMessages(@PathVariable ChatId chatId) {
        return chatReader.getMessages(chatId);
    }

    @PostMapping
    public ChatDto createChat(@RequestBody ChatDto chatDto) {
        return chatService.createChat(chatDto);
    }

    @PutMapping("{chatId}")
    public MessageDto addMessage(@PathVariable ChatId chatId, @RequestBody MessageDto messageDto) {
        return chatService.addMessage(chatId, messageDto);
    }
}
