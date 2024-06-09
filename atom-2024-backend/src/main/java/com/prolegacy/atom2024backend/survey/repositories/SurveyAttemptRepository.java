package com.prolegacy.atom2024backend.survey.repositories;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.survey.entities.SurveyAttempt;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyAttemptRepository extends JpaRepository<SurveyAttempt, SurveyAttemptId> {
    default Optional<SurveyAttempt> findActiveLastSurveyAttemptForUser(UserId userId, SurveyId surveyId) {
        return this.findByUserIdAndSurveyIdAndLastAttemptIsTrueAndFinishDateIsNull(userId, surveyId);
    }

    default Optional<SurveyAttempt> findLastSurveyAttemptForUser(UserId userId, SurveyId surveyId) {
        return this.findByUserIdAndSurveyIdAndLastAttemptIsTrue(userId, surveyId);
    }

    Optional<SurveyAttempt> findByUserIdAndSurveyIdAndLastAttemptIsTrueAndFinishDateIsNull(UserId userId, SurveyId surveyId);

    Optional<SurveyAttempt> findByUserIdAndSurveyIdAndLastAttemptIsTrue(UserId userId, SurveyId surveyId);

    List<SurveyAttempt> findBySurveyId(SurveyId surveyId);
}
