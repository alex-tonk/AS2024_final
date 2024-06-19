package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.dto.LessonDto;
import com.prolegacy.atom2024backend.dto.TopicDto;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.readers.LessonReader;
import com.prolegacy.atom2024backend.readers.TopicReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("topics")
@TypescriptEndpoint
public class TopicController {

    @Autowired
    private TopicReader topicReader;

    @Autowired
    private LessonReader lessonReader;

    @GetMapping
    public List<TopicDto> getTopics() {
        return topicReader.getTopics();
    }

    @GetMapping("{topicId}/lessons")
    public List<LessonDto> getTopicLessons(@PathVariable TopicId topicId) {
        return lessonReader.getLessonsForTopic(topicId);
    }

    @GetMapping("{topicId}/lessons/attempts")
    public List<LessonDto> getTopicLessonsWithLastAttempts(@PathVariable TopicId topicId) {
        return lessonReader.getLessonsForTopic(topicId, true);
    }

    @GetMapping("{topicId}/graduates")
    public List<UserDto> getUsersWithFinishedTopic(@PathVariable TopicId topicId) {
        return topicReader.getUsersWithFinishedTopic(topicId);
    };
}
