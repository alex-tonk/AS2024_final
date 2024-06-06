package com.prolegacy.atom2024backend.survey.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.survey.dto.SurveyAttemptAnswerDto;
import com.prolegacy.atom2024backend.survey.entities.QSurveyAttemptAnswerView;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SurveyAttemptAnswerReader {

    private static final QSurveyAttemptAnswerView surveyAttemptAnswer = QSurveyAttemptAnswerView.surveyAttemptAnswerView;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<SurveyAttemptAnswerDto> getSurveyAttemptAnswers(SurveyAttemptId surveyAttemptId) {
        return baseQuery()
                .where(surveyAttemptAnswer.surveyAttemptId.eq(surveyAttemptId))
                .fetch();
    }

    private JPAQuery<SurveyAttemptAnswerDto> baseQuery() {
        return queryFactory.from(surveyAttemptAnswer)
                .selectDto(SurveyAttemptAnswerDto.class);
    }
}
