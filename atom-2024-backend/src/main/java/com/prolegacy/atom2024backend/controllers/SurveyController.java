package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("surveys")
public class SurveyController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("files")
    public FileId uploadSurveyFile(@RequestBody MultipartFile file) {
        return fileUploadService.uploadFile(file).getId();
    }

    @GetMapping("files/{fileId}")
    public Resource uploadSurveyFile(@PathVariable FileId fileId) {
        return fileUploadService.serveFile(fileId);
    }
}
