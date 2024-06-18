package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("lessons")
@TypescriptEndpoint
public class LessonController {

    @Autowired
    private FileUploadService fileUploadService;

    @TypescriptIgnore
    @GetMapping("files/{fileId}")
    public Resource getLessonFile(@PathVariable FileId fileId) {
        return fileUploadService.serveFile(fileId);
    }
}
