package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.chat.AttachmentDto;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.chat.AttachmentId;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import com.prolegacy.atom2024backend.entities.ids.chat.MessageId;
import com.prolegacy.atom2024backend.readers.ChatReader;
import com.prolegacy.atom2024backend.services.ChatService;
import com.prolegacy.atom2024backend.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

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
    @Autowired
    private UserReader userReader;

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

    @PostMapping("attachments")
    @TypescriptIgnore
    public AttachmentDto uploadFile(@RequestParam MultipartFile file) {
        return new AttachmentDto(null, null, fileUploadService.uploadFile(file).getId(), file.getOriginalFilename());
    }

    @DeleteMapping("attachments/{fileId}")
    @TypescriptIgnore
    public void deleteFile(@PathVariable FileId fileId) {
        fileUploadService.deleteFile(fileId);
    }

    @GetMapping("{chatId}/messages/{messageId}/attachments/{attachmentId}")
    @TypescriptIgnore
    public Resource getAttachment(@PathVariable ChatId chatId,
                                  @PathVariable MessageId messageId,
                                  @PathVariable AttachmentId attachmentId) {
        ChatDto chat = chatReader.getChat(chatId);
        User user = userProvider.get();
        if (chat.getMembers().stream().noneMatch(userDto -> Objects.equals(userDto.getId(), user.getId()))) {
            throw new BusinessLogicException("Вы не являетесь учатником этого чата");
        }
        AttachmentDto attachmentDto = chat.getMessages().stream()
                .filter(msg -> msg.getId().equals(messageId))
                .flatMap(msg -> msg.getAttachments().stream())
                .filter(att -> att.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException("Приложение не найдено"));

        return fileUploadService.serveFile(attachmentDto.getFileId());
    }

    @GetMapping("all-users")
    public List<UserDto> getUsers() {
        return userReader.getUsers();
    }
}
