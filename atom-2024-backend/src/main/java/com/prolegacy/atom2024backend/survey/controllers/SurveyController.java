package com.prolegacy.atom2024backend.survey.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.survey.dto.SurveyDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.readers.SurveyReader;
import com.prolegacy.atom2024backend.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("surveys")
@TypescriptEndpoint
public class SurveyController {

    @Autowired
    private SurveyReader surveyReader;

    @Autowired
    private SurveyService surveyService;

    @GetMapping
    public List<SurveyDto> getSurveys() {
        return surveyReader.getSurveys();
    }

    @GetMapping("{surveyId}")
    public SurveyDto getSurvey(@PathVariable SurveyId surveyId) {
        return surveyReader.getSurvey(surveyId);
    }

    @PreAuthorize("hasAnyRole('admin', 'methodist')")
    @PostMapping
    public SurveyDto createSurvey(@RequestBody SurveyDto surveyDto) {
        return surveyService.createSurvey(surveyDto);
    }

    @PreAuthorize("hasAnyRole('admin', 'methodist')")
    @PutMapping("{surveyId}")
    public SurveyDto updateSurvey(@PathVariable SurveyId surveyId, @RequestBody SurveyDto surveyDto) {
        return surveyService.updateSurvey(surveyId, surveyDto);
    }

    @PreAuthorize("hasAnyRole('admin', 'methodist')")
    @DeleteMapping("{surveyId}")
    public void deleteSurvey(@PathVariable SurveyId surveyId) {
        surveyService.deleteSurvey(surveyId);
    }

    @TypescriptIgnore
    @PreAuthorize("hasAnyRole('admin', 'methodist')")
    @PostMapping("/files")
    public FileId uploadQuestionFile(@RequestBody MultipartFile file) {
        return surveyService.uploadQuestionFile(file);
    }

    @TypescriptIgnore
    @PreAuthorize("hasAnyRole('admin', 'methodist')")
    @DeleteMapping("/files/{fileId}")
    public boolean deleteQuestionFile(@PathVariable FileId fileId) {
        return surveyService.deleteQuestionFile(fileId);
    }

    @TypescriptIgnore
    @GetMapping("/files/{fileId}")
    public Resource serveFile(@PathVariable FileId fileId) {
        return surveyService.serveQuestionFile(fileId);
    }
}
