package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.dto.LessonDto;
import com.prolegacy.atom2024backend.dto.TaskDto;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.readers.LessonReader;
import com.prolegacy.atom2024backend.readers.TaskReader;
import com.prolegacy.atom2024backend.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("tasks")
@TypescriptEndpoint
public class TaskController {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private LessonReader lessonReader;
    @Autowired
    private TaskReader taskReader;

    @TypescriptIgnore
    @GetMapping("files/{fileId}")
    public Resource getLessonFile(@PathVariable FileId fileId) {
        return fileUploadService.serveFile(fileId);
    }

    @GetMapping
    public List<LessonDto> getRecommendations(@RequestParam LessonId lessonId) {
        return lessonReader.getRecommendations(lessonId);
    }

    @GetMapping("stats")
    public List<TaskDto> getTasksWithStats() {
        return taskReader.getTasksWithStats();
    }
}
