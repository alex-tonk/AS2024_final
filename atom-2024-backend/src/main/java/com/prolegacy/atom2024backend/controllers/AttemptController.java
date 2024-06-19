package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.dto.AttemptDto;
import com.prolegacy.atom2024backend.entities.ids.*;
import com.prolegacy.atom2024backend.enums.AttemptStatus;
import com.prolegacy.atom2024backend.readers.AttemptReader;
import com.prolegacy.atom2024backend.services.AttemptService;
import com.prolegacy.atom2024backend.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("attempts")
@TypescriptEndpoint
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    @Autowired
    private AttemptReader attemptReader;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private FileUploadService fileUploadService;

    @PreAuthorize("hasRole('student')")
    @PostMapping("topics/{topicId}/lessons/{lessonId}/tasks/{taskId}")
    public AttemptDto startNewAttempt(@PathVariable TopicId topicId,
                                      @PathVariable LessonId lessonId,
                                      @PathVariable TaskId taskId) {
        return attemptService.startNewAttempt(topicId, lessonId, taskId);
    }

    @PreAuthorize("hasRole('student')")
    @GetMapping("topics/{topicId}/lessons/{lessonId}/tasks/{taskId}")
    public AttemptDto getLastAttempt(@PathVariable TopicId topicId,
                                     @PathVariable LessonId lessonId,
                                     @PathVariable TaskId taskId) {
        return attemptReader.getLastAttempt(topicId, lessonId, taskId, userProvider.get().getId());
    }

    @PreAuthorize("hasAnyRole('student', 'tutor')")
    @GetMapping("{attemptId}")
    public AttemptDto getAttempt(@PathVariable AttemptId attemptId) {
        return attemptReader.getAttempt(attemptId);
    }

    @PreAuthorize("hasRole('student')")
    @PutMapping("{attemptId}")
    public AttemptDto finishAttempt(@PathVariable AttemptId attemptId,
                                        @RequestBody AttemptDto attemptDto) {
        return attemptService.finishAttempt(attemptId, attemptDto);
    }

    @PreAuthorize("hasRole('tutor')")
    @PatchMapping("{attemptId}")
    public AttemptDto setTutorMark(@PathVariable AttemptId attemptId,
                                        @RequestBody AttemptDto attemptDto) {
        return attemptService.setTutorMark(attemptId, attemptDto);
    }

    @PreAuthorize("hasRole('student')")
    @PostMapping("files")
    @TypescriptIgnore
    public FileId uploadAttemptFile(@RequestBody MultipartFile file) {
        return fileUploadService.uploadFile(file).getId();
    }

    @GetMapping
    public List<AttemptDto> getAttempts(@RequestParam Optional<UserId> userId,
                                        @RequestParam Optional<AttemptStatus> status) {
        return attemptReader.getAttempts(userId, status);
    }
}
