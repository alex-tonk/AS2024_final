package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.dto.AttemptDto;
import com.prolegacy.atom2024backend.entities.ids.AttemptId;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.entities.ids.TaskId;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.readers.AttemptReader;
import com.prolegacy.atom2024backend.services.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
