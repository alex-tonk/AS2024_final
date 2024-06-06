package com.prolegacy.atom2024backend.survey.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.survey.dto.SurveyDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.entities.*;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.exceptions.SurveyNotFoundException;
import com.prolegacy.atom2024backend.survey.meta.answers.*;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerQuestionMeta;
import com.prolegacy.atom2024backend.survey.readers.SurveyReader;
import com.prolegacy.atom2024backend.survey.repositories.SurveyAttemptRepository;
import com.prolegacy.atom2024backend.survey.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyReader surveyReader;

    @Autowired
    private SurveyAttemptRepository surveyAttemptRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SurveyDto createSurvey(SurveyDto surveyDto) {
        Survey survey = new Survey(surveyDto);
        validateQuestionsNotEmpty(surveyDto);
        addQuestionsToSurvey(surveyDto, survey);
        this.surveyRepository.save(survey);
        return this.surveyReader.getSurvey(survey.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SurveyDto updateSurvey(SurveyId surveyId, SurveyDto surveyDto) {
        Survey survey = Optional.ofNullable(surveyId)
                .flatMap(this.surveyRepository::findById)
                .orElseThrow(SurveyNotFoundException::new);
        validateNoAttemptsExist(surveyId);
        survey.update(surveyDto);
        survey.clearQuestions();
        validateQuestionsNotEmpty(surveyDto);
        addQuestionsToSurvey(surveyDto, survey);
        this.surveyRepository.save(survey);
        return this.surveyReader.getSurvey(survey.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteSurvey(SurveyId surveyId) {
        Survey survey = Optional.ofNullable(surveyId)
                .flatMap(this.surveyRepository::findById)
                .orElseThrow(SurveyNotFoundException::new);
        validateNoAttemptsExist(surveyId);
        this.surveyRepository.delete(survey);
    }

    private static void addQuestionsToSurvey(SurveyDto surveyDto, Survey survey) {
        surveyDto.getQuestions().forEach(questionDto -> {
            validateQuestionType(questionDto);
            try {
                switch (questionDto.getType()) {
                    case STRING -> {
                        StringSurveyQuestionCorrectAnswerMeta correctAnswerMeta = mapper.treeToValue(questionDto.getCorrectAnswerMeta(), StringSurveyQuestionCorrectAnswerMeta.class);
                        survey.addQuestion(
                                new StringSurveyQuestion(
                                        survey,
                                        questionDto,
                                        correctAnswerMeta
                                )
                        );
                    }
                    case NUMBER -> {
                        NumberSurveyQuestionAnswerMeta correctAnswerMeta = mapper.treeToValue(questionDto.getCorrectAnswerMeta(), NumberSurveyQuestionAnswerMeta.class);
                        survey.addQuestion(
                                new NumberSurveyQuestion(
                                        survey,
                                        questionDto,
                                        correctAnswerMeta
                                )
                        );
                    }
                    case RADIO_BUTTON -> {
                        PredefinedAnswerQuestionMeta meta = mapper.treeToValue(questionDto.getMeta(), PredefinedAnswerQuestionMeta.class);
                        RadioButtonSurveyQuestionAnswerMeta correctAnswerMeta = mapper.treeToValue(questionDto.getCorrectAnswerMeta(), RadioButtonSurveyQuestionAnswerMeta.class);
                        survey.addQuestion(
                                new RadioButtonSurveyQuestion(
                                        survey,
                                        questionDto,
                                        meta,
                                        correctAnswerMeta
                                )
                        );
                    }
                    case CHECKBOX -> {
                        PredefinedAnswerQuestionMeta meta = mapper.treeToValue(questionDto.getMeta(), PredefinedAnswerQuestionMeta.class);
                        CheckboxSurveyQuestionAnswerMeta correctAnswerMeta = mapper.treeToValue(questionDto.getCorrectAnswerMeta(), CheckboxSurveyQuestionAnswerMeta.class);
                        survey.addQuestion(
                                new CheckboxSurveyQuestion(
                                        survey,
                                        questionDto,
                                        meta,
                                        correctAnswerMeta
                                )
                        );
                    }
                    case RANKING -> {
                        PredefinedAnswerQuestionMeta meta = mapper.treeToValue(questionDto.getMeta(), PredefinedAnswerQuestionMeta.class);
                        RankingSurveyQuestionAnswerMeta correctAnswerMeta = mapper.treeToValue(questionDto.getCorrectAnswerMeta(), RankingSurveyQuestionAnswerMeta.class);
                        survey.addQuestion(
                                new RankingSurveyQuestion(
                                        survey,
                                        questionDto,
                                        meta,
                                        correctAnswerMeta
                                )
                        );
                    }
                }
            } catch (JsonProcessingException e) {
                throw new BusinessLogicException("Некорректная настройка вопроса");
            }
        });
    }

    private static void validateQuestionType(SurveyQuestionDto question) {
        if (question.getType() == null) {
            throw new BusinessLogicException("Не задан тип вопроса");
        }
    }

    private static void validateQuestionsNotEmpty(SurveyDto surveyDto) {
        boolean noQuestionsProvided = Optional.ofNullable(surveyDto)
                .map(SurveyDto::getQuestions)
                .map(List::isEmpty)
                .orElse(true);
        if (noQuestionsProvided) {
            throw new BusinessLogicException("Не задан список вопросов");
        }
    }

    private void validateNoAttemptsExist(SurveyId surveyId) {
        if (!surveyAttemptRepository.findBySurveyId(surveyId).isEmpty()) {
            throw new BusinessLogicException("Невозможно изменить или удалить тестирование, которое уже начали проходить обучающиеся");
        }
    }
}
