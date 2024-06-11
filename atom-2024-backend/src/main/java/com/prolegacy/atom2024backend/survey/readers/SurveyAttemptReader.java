package com.prolegacy.atom2024backend.survey.readers;

import com.prolegacy.atom2024backend.common.auth.entities.QUser;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptDto;
import com.prolegacy.atom2024backend.survey.entities.QSurveyAttempt;
import com.prolegacy.atom2024backend.survey.entities.QSurveyAttemptAnswerView;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class SurveyAttemptReader {

    private static final QSurveyAttempt surveyAttempt = QSurveyAttempt.surveyAttempt;
    private static final QUser user = QUser.user;
    private static final QSurveyAttemptAnswerView answersForCounting = new QSurveyAttemptAnswerView("answersForCounting");

    @Autowired
    private JPAQueryFactory queryFactory;

    public SurveyAttemptDto getSurveyAttempt(SurveyAttemptId surveyAttemptId) {
        return baseQuery().where(surveyAttempt.id.eq(surveyAttemptId)).fetchFirst();
    }

    public Expression<Long> surveyAttemptCorrectAnswerCount(SurveyAttemptId surveyAttemptId) {
        return this.surveyAttemptCorrectAnswerCount(Expressions.asNumber(surveyAttemptId));
    }

    public Expression<Long> surveyAttemptCorrectAnswerCount(NumberExpression<SurveyAttemptId> surveyAttemptId) {
        return Expressions.cases()
                .when(surveyAttemptId.isNotNull())
                .then(
                        queryFactory.from(answersForCounting)
                                .where(answersForCounting.surveyAttemptId.eq(surveyAttemptId))
                                .where(answersForCounting.correct.eq(true))
                                .select(answersForCounting.id.count())
                ).otherwise(Expressions.nullExpression(Long.class));
    }

    private JPAQuery<SurveyAttemptDto> baseQuery() {
        return queryFactory.from(surveyAttempt)
                .leftJoin(user).on(user.id.eq(surveyAttempt.user.id))
                .selectDto(
                        SurveyAttemptDto.class,
                        Expressions.as(surveyAttemptCorrectAnswerCount(surveyAttempt.id), "correctAnswerCount"),
                        surveyAttempt.survey.id.as("surveyId")
                );
    }
}
