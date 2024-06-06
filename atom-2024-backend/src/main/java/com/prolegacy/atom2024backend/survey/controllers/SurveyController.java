package com.prolegacy.atom2024backend.survey.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptAnswerDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.readers.SurveyReader;
import com.prolegacy.atom2024backend.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public SurveyDto createSurvey(@RequestBody SurveyDto surveyDto) {
        return surveyService.createSurvey(surveyDto);
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("{surveyId}")
    public SurveyDto updateSurvey(@PathVariable SurveyId surveyId, @RequestBody SurveyDto surveyDto) {
        return surveyService.updateSurvey(surveyId, surveyDto);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("{surveyId}")
    public void deleteSurvey(@PathVariable SurveyId surveyId) {
        surveyService.deleteSurvey(surveyId);
    }
}
