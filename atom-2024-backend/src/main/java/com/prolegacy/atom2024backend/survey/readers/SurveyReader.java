package com.prolegacy.atom2024backend.survey.readers;

import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.survey.dto.SurveyDto;
import com.prolegacy.atom2024backend.survey.entities.QSurvey;
import com.prolegacy.atom2024backend.survey.entities.QSurveyAttempt;
import com.prolegacy.atom2024backend.survey.entities.QSurveyQuestion;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SurveyReader {

    private static final QSurvey survey = QSurvey.survey;
    private static final QSurveyQuestion questionsForCounting = new QSurveyQuestion("questionsForCounting");
    private static final QSurveyAttempt lastSurveyAttempt = new QSurveyAttempt("lastSurveyAttempt");

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private SurveyAttemptReader surveyAttemptReader;

    public List<SurveyDto> getSurveys() {
        return baseQuery().fetch();
    }

    public SurveyDto getSurvey(SurveyId surveyId) {
        return baseQuery()
                .where(survey.id.eq(surveyId))
                .fetchFirst();
    }

    private JPAQuery<SurveyDto> baseQuery() {
        Expression<Long> questionCount = queryFactory.from(questionsForCounting)
                .where(questionsForCounting.survey.id.eq(survey.id))
                .select(questionsForCounting.id.count());

        return queryFactory.from(survey)
                .leftJoin(lastSurveyAttempt).on(
                        lastSurveyAttempt.survey.id.eq(survey.id)
                                .and(lastSurveyAttempt.user.id.eq(userProvider.get().getId()))
                                .and(lastSurveyAttempt.lastAttempt.eq(true))
                )
                .selectDto(
                        SurveyDto.class,
                        Expressions.as(questionCount, ("questionCount")),
                        Expressions.as(surveyAttemptReader.surveyAttemptCorrectAnswerCount(lastSurveyAttempt.id), "lastSurveyAttempt.correctAnswerCount")
                );
    }
}
