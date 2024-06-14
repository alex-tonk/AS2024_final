package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.dto.chat.AttachmentDto;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import com.prolegacy.atom2024backend.readers.ChatReader;
import com.prolegacy.atom2024backend.services.ChatService;
import com.prolegacy.atom2024backend.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public List<ChatDto> getChats() {
        return chatReader.getChats(userProvider.get().getId());
    }

    @GetMapping("{chatId}")
    public ChatDto getChat(@PathVariable ChatId chatId) {
        return chatReader.getChat(chatId);
    }

    @PostMapping
    public ChatDto createChat(@RequestBody ChatDto chatDto) {
        return chatService.createChat(chatDto);
    }

    @PutMapping("{chatId}")
    public void addMessage(@PathVariable ChatId chatId, @RequestBody MessageDto messageDto) {
        chatService.addMessage(chatId, messageDto);
    }

    @DeleteMapping("{chatId}/leave")
    public void leaveChat(@PathVariable ChatId chatId) {
        chatService.leaveChat(chatId);
    }

    @PostMapping("{chatId}/users/{userId}")
    public void addUserToChat(@PathVariable ChatId chatId, @PathVariable UserId userId) {
        chatService.addUserToChat(chatId, userId);
    }

    @PostMapping("attachment")
    @TypescriptIgnore
    public AttachmentDto uploadFile(@RequestParam MultipartFile file) {
        return new AttachmentDto(null, null, fileUploadService.uploadFile(file).getId(), file.getOriginalFilename());
    }

    @DeleteMapping("attachment/{fileId}")
    @TypescriptIgnore
    public void deleteFile(@PathVariable FileId fileId) {
        fileUploadService.deleteFile(fileId);
    }

    @GetMapping("attachment/{fileId}")
    @TypescriptIgnore
    public Resource getAttachment(@PathVariable FileId fileId) {
        return fileUploadService.serveFile(fileId);
    }
}
