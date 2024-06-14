package com.prolegacy.atom2024backend.survey.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.entities.QFile;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.entities.QSurveyQuestionView;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyQuestionId;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SurveyQuestionReader {

    private static final QSurveyQuestionView surveyQuestionView = QSurveyQuestionView.surveyQuestionView;
    private static final QFile file = QFile.file;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<SurveyQuestionDto> getSurveyQuestions(SurveyId surveyId, boolean hideCorrectAnswers) {
        return baseQuery(hideCorrectAnswers)
                .where(surveyQuestionView.surveyId.eq(surveyId))
                .fetch();
    }

    public SurveyQuestionDto getSurveyQuestion(SurveyId surveyId, SurveyQuestionId questionId, boolean hideCorrectAnswers) {
        return baseQuery(hideCorrectAnswers)
                .where(surveyQuestionView.surveyId.eq(surveyId))
                .where(surveyQuestionView.id.eq(questionId))
                .fetchFirst();
    }

    private JPAQuery<SurveyQuestionDto> baseQuery(boolean hideCorrectAnswers) {
        return queryFactory.from(surveyQuestionView)
                .leftJoin(file).on(file.id.eq(surveyQuestionView.fileId))
                .selectDto(
                        SurveyQuestionDto.class,
                        file.id.as("fileId"),
                        file.fileName.as("fileName"),
                        hideCorrectAnswers ? Expressions.as(Expressions.nullExpression(JsonNode.class), "correctAnswerMeta") : null
                );
    }
}
