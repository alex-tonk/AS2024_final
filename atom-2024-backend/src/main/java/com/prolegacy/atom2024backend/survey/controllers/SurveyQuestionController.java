package com.prolegacy.atom2024backend.survey.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.readers.SurveyQuestionReader;
import com.prolegacy.atom2024backend.survey.services.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("surveys/{surveyId}/questions")
@TypescriptEndpoint
public class SurveyQuestionController {

    @Autowired
    private SurveyQuestionReader surveyQuestionReader;

    @GetMapping
    public List<SurveyQuestionDto> getSurveyQuestions(@PathVariable SurveyId surveyId) {
        return surveyQuestionReader.getSurveyQuestions(surveyId, true);
    }

    @PreAuthorize("hasAnyRole('admin', 'methodist')")
    @GetMapping("/answers")
    public List<SurveyQuestionDto> getSurveyQuestionsWithAnswers(@PathVariable SurveyId surveyId) {
        return surveyQuestionReader.getSurveyQuestions(surveyId, false);
    }
}
