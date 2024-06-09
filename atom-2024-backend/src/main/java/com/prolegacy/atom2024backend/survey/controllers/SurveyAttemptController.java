package com.prolegacy.atom2024backend.survey.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptAnswerDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.readers.SurveyAttemptAnswerReader;
import com.prolegacy.atom2024backend.survey.readers.SurveyAttemptReader;
import com.prolegacy.atom2024backend.survey.services.SurveyAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("surveys/{surveyId}/attempts")
@TypescriptEndpoint
public class SurveyAttemptController {

    @Autowired
    private SurveyAttemptService surveyAttemptService;

    @Autowired
    private SurveyAttemptReader surveyAttemptReader;

    @Autowired
    private SurveyAttemptAnswerReader surveyAttemptAnswerReader;

    @GetMapping("{surveyAttemptId}")
    public SurveyAttemptDto getSurveyAttempt(@PathVariable SurveyId surveyId,
                                             @PathVariable SurveyAttemptId surveyAttemptId) {
        return surveyAttemptReader.getSurveyAttempt(surveyAttemptId);
    }

    @PostMapping
    public SurveyAttemptDto beginSurveyAttempt(@PathVariable SurveyId surveyId) {
        return surveyAttemptService.beginSurveyAttempt(surveyId);
    }

    @PostMapping("{surveyAttemptId}/answers")
    public void saveAnswer(@PathVariable SurveyId surveyId,
                           @PathVariable SurveyAttemptId surveyAttemptId,
                           @RequestBody SurveyAttemptAnswerDto answerDto) {
        surveyAttemptService.saveAnswer(surveyId, surveyAttemptId, answerDto);
    }

    @PostMapping("{surveyAttemptId}")
    public SurveyAttemptDto finishAttempt(@PathVariable SurveyId surveyId,
                                          @PathVariable SurveyAttemptId surveyAttemptId,
                                          @RequestBody SurveyAttemptDto attemptDto) {
        return surveyAttemptService.finishAttempt(surveyId, surveyAttemptId, attemptDto);
    }

    @GetMapping("{surveyAttemptId}/answers")
    public List<SurveyAttemptAnswerDto> getAttemptAnswers(@PathVariable SurveyId surveyId,
                                                          @PathVariable SurveyAttemptId surveyAttemptId) {
        return surveyAttemptAnswerReader.getSurveyAttemptAnswers(surveyAttemptId);
    }
}
