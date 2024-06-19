package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("diplomas/{topicId}/{userId}")
    public Resource printDiploma(@PathVariable TopicId topicId, @PathVariable UserId userId) {
        return reportService.createDiploma(userId, topicId);
    }

    @GetMapping("diplomas/{topicId}/{userId}/specifications")
    public Resource printDiplomaSpec(@PathVariable TopicId topicId, @PathVariable UserId userId) {
        return reportService.createDiplomaSpec(userId, topicId);
    }

    @GetMapping("diplomas/{topicId}/{userId}/full")
    public Resource printDiplomaFull(@PathVariable TopicId topicId, @PathVariable UserId userId) {
        return reportService.createDiplomaFull(userId, topicId);
    }
}
