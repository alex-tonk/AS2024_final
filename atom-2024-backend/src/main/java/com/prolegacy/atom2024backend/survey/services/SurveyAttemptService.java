package com.prolegacy.atom2024backend.survey.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptAnswerDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptDto;
import com.prolegacy.atom2024backend.survey.entities.*;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.prolegacy.atom2024backend.survey.exceptions.SurveyAttemptNotFoundException;
import com.prolegacy.atom2024backend.survey.exceptions.SurveyNotFoundException;
import com.prolegacy.atom2024backend.survey.meta.answers.*;
import com.prolegacy.atom2024backend.survey.readers.SurveyAttemptReader;
import com.prolegacy.atom2024backend.survey.repositories.SurveyAttemptRepository;
import com.prolegacy.atom2024backend.survey.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SurveyAttemptService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SurveyAttemptReader surveyAttemptReader;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private SurveyAttemptRepository surveyAttemptRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SurveyAttemptDto beginSurveyAttempt(SurveyId surveyId) {
        Survey survey = Optional.ofNullable(surveyId)
                .flatMap(this.surveyRepository::findById)
                .orElseThrow(SurveyNotFoundException::new);
        Optional<SurveyAttempt> activeLastAttempt = this.surveyAttemptRepository.findActiveLastSurveyAttemptForUser(userProvider.get().getId(), survey.getId());
        activeLastAttempt.ifPresent(surveyAttempt -> {
            throw new BusinessLogicException("Завершите предыдущую попытку прохождения тестирования прежде чем начинать новую");
        });
        Optional<SurveyAttempt> lastAttempt = this.surveyAttemptRepository.findLastSurveyAttemptForUser(userProvider.get().getId(), survey.getId());
        lastAttempt.ifPresent(a -> a.setLastAttempt(false));
        SurveyAttempt surveyAttempt = new SurveyAttempt(this.userProvider.get(), survey);
        this.surveyAttemptRepository.save(surveyAttempt);
        return this.surveyAttemptReader.getSurveyAttempt(surveyAttempt.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void saveAnswer(SurveyId surveyId, SurveyAttemptId surveyAttemptId, SurveyAttemptAnswerDto answerDto) {
        SurveyAttempt surveyAttempt = Optional.ofNullable(surveyAttemptId)
                .flatMap(this.surveyAttemptRepository::findById)
                .orElseThrow(SurveyAttemptNotFoundException::new);

        validateAttemptBelongsToSurvey(surveyAttempt, surveyId);
        validateAttemptIsNotFinished(surveyAttempt);
        validateAnswerHasQuestion(answerDto);
        validateSurveyAttemptUser(surveyAttempt);

        SurveyQuestion question = surveyAttempt.getSurvey().getQuestion(answerDto.getQuestionId())
                .orElseThrow(() -> new BusinessLogicException("Не найден вопрос для сохранения ответа"));

        surveyAttempt.getAnswerForQuestion(answerDto.getQuestionId())
                .ifPresentOrElse(
                        answer -> updateAnswer(answer, answerDto),
                        () -> {
                            SurveyAttemptAnswer<?> answer = createAnswer(question, surveyAttempt, answerDto);
                            surveyAttempt.addAnswer(answer);
                        }
                );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SurveyAttemptDto finishAttempt(SurveyId surveyId, SurveyAttemptId surveyAttemptId, SurveyAttemptDto attemptDto) {
        SurveyAttempt surveyAttempt = Optional.ofNullable(surveyAttemptId)
                .flatMap(this.surveyAttemptRepository::findById)
                .orElseThrow(SurveyAttemptNotFoundException::new);
        validateAttemptBelongsToSurvey(surveyAttempt, surveyId);
        validateAttemptIsNotFinished(surveyAttempt);
        validateSurveyAttemptUser(surveyAttempt);

        Map<SurveyQuestionId, SurveyQuestion> questions = surveyAttempt.getSurvey().getQuestions()
                .stream()
                .collect(Collectors.toMap(
                        SurveyQuestion::getId,
                        Function.identity()
                ));

        Optional.ofNullable(attemptDto.getAnswers())
                .orElseGet(ArrayList::new)
                .forEach(answerDto -> {
                    validateAnswerHasQuestion(answerDto);
                    SurveyQuestion question = Optional.ofNullable(questions.get(answerDto.getQuestionId()))
                            .orElseThrow(() -> new BusinessLogicException("Не найден вопрос для сохранения ответа"));
                    surveyAttempt.getAnswerForQuestion(answerDto.getQuestionId())
                            .ifPresentOrElse(
                                    answer -> updateAnswer(answer, answerDto),
                                    () -> {
                                        SurveyAttemptAnswer<?> answer = createAnswer(question, surveyAttempt, answerDto);
                                        surveyAttempt.addAnswer(answer);
                                    }
                            );
                });
        surveyAttempt.finish();
        return this.surveyAttemptReader.getSurveyAttempt(surveyAttempt.getId());
    }

    private static SurveyAttemptAnswer<?> createAnswer(SurveyQuestion question,
                                                       SurveyAttempt surveyAttempt,
                                                       SurveyAttemptAnswerDto answerDto) {
        try {
            if (question instanceof CheckboxSurveyQuestion checkboxSurveyQuestion) {
                CheckboxSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), CheckboxSurveyQuestionAnswerMeta.class);
                return new CheckboxSurveyAttemptAnswer(surveyAttempt, checkboxSurveyQuestion, answerMeta);
            } else if (question instanceof NumberSurveyQuestion numberSurveyQuestion) {
                NumberSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), NumberSurveyQuestionAnswerMeta.class);
                return new NumberSurveyAttemptAnswer(surveyAttempt, numberSurveyQuestion, answerMeta);
            } else if (question instanceof RadioButtonSurveyQuestion radioButtonSurveyQuestion) {
                RadioButtonSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), RadioButtonSurveyQuestionAnswerMeta.class);
                return new RadioButtonSurveyAttemptAnswer(surveyAttempt, radioButtonSurveyQuestion, answerMeta);
            } else if (question instanceof RankingSurveyQuestion rankingSurveyQuestion) {
                RankingSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), RankingSurveyQuestionAnswerMeta.class);
                return new RankingSurveyAttemptAnswer(surveyAttempt, rankingSurveyQuestion, answerMeta);
            } else if (question instanceof StringSurveyQuestion stringSurveyQuestion) {
                StringSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), StringSurveyQuestionAnswerMeta.class);
                return new StringSurveyAttemptAnswer(surveyAttempt, stringSurveyQuestion, answerMeta);
            } else {
                throw new BusinessLogicException("Неизвестный тип вопроса для сохранения ответа");
            }
        } catch (JsonProcessingException e) {
            throw new BusinessLogicException("Некорректный формат ответа");
        }
    }

    private static void updateAnswer(SurveyAttemptAnswer<?> answer, SurveyAttemptAnswerDto answerDto) {
        try {
            if (answer instanceof CheckboxSurveyAttemptAnswer checkboxSurveyAttemptAnswer) {
                CheckboxSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), CheckboxSurveyQuestionAnswerMeta.class);
                checkboxSurveyAttemptAnswer.updateAnswer(answerMeta);
            } else if (answer instanceof NumberSurveyAttemptAnswer numberSurveyAttemptAnswer) {
                NumberSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), NumberSurveyQuestionAnswerMeta.class);
                numberSurveyAttemptAnswer.updateAnswer(answerMeta);
            } else if (answer instanceof RadioButtonSurveyAttemptAnswer radioButtonSurveyAttemptAnswer) {
                RadioButtonSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), RadioButtonSurveyQuestionAnswerMeta.class);
                radioButtonSurveyAttemptAnswer.updateAnswer(answerMeta);
            } else if (answer instanceof RankingSurveyAttemptAnswer rankingSurveyAttemptAnswer) {
                RankingSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), RankingSurveyQuestionAnswerMeta.class);
                rankingSurveyAttemptAnswer.updateAnswer(answerMeta);
            } else if (answer instanceof StringSurveyAttemptAnswer stringSurveyAttemptAnswer) {
                StringSurveyQuestionAnswerMeta answerMeta = mapper.treeToValue(answerDto.getAnswer(), StringSurveyQuestionAnswerMeta.class);
                stringSurveyAttemptAnswer.updateAnswer(answerMeta);
            } else {
                throw new BusinessLogicException("Неизвестный тип вопроса для сохранения ответа");
            }
        } catch (JsonProcessingException e) {
            throw new BusinessLogicException("Некорректный формат ответа");
        }
    }

    private static void validateAnswerHasQuestion(SurveyAttemptAnswerDto answerDto) {
        if (answerDto.getQuestionId() == null) {
            throw new BusinessLogicException("Не задан вопрос для сохранения ответа");
        }
    }

    private static void validateAttemptBelongsToSurvey(SurveyAttempt surveyAttempt, SurveyId surveyId) {
        if (!Objects.equals(surveyAttempt.getSurvey().getId(), surveyId)) {
            throw new BusinessLogicException("Ответ не соответствует проходимому тестированию");
        }
    }

    private static void validateAttemptIsNotFinished(SurveyAttempt surveyAttempt) {
        if (surveyAttempt.getFinishDate() != null) {
            throw new BusinessLogicException("Попытка уже завершена");
        }
    }

    private void validateSurveyAttemptUser(SurveyAttempt surveyAttempt) {
        if (!Objects.equals(surveyAttempt.getUser().getId(), userProvider.get().getId())) {
            throw new BusinessLogicException("Нельзя проходить тестирование за другого человека");
        }
    }
}
