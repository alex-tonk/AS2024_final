package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.AttemptCheckResultDto;
import com.prolegacy.atom2024backend.dto.AttemptDto;
import com.prolegacy.atom2024backend.dto.FeatureDto;
import com.prolegacy.atom2024backend.entities.*;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.entities.ids.TaskId;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class AttemptReader {
    private static final QAttempt attempt = QAttempt.attempt;
    private static final QAttemptCheckResult attemptCheckResult = QAttemptCheckResult.attemptCheckResult;
    private static final QFeature feature = QFeature.feature;
    private static final QFile file = QFile.file;

    @Autowired
    private JPAQueryFactory queryFactory;

    public AttemptDto getLastAttempt(TopicId topicId, LessonId lessonId, TaskId taskId, UserId userId) {
        AttemptDto lastAttempt = baseQuery()
                .where(attempt.topic.id.eq(topicId))
                .where(attempt.lesson.id.eq(lessonId))
                .where(attempt.task.id.eq(taskId))
                .where(attempt.user.id.eq(userId))
                .where(attempt.isLastAttempt.isTrue())
                .fetchFirst();

        if (lastAttempt == null) return null;

        setAutoCheckResults(lastAttempt);

        setTutorCheckResults(lastAttempt);

        lastAttempt.setFiles(
                queryFactory.from(file)
                        .innerJoin(attempt).on(attempt.id.eq(lastAttempt.getId()).and(attempt.files.any().id.eq(file.id)))
                        .selectDto(File.class)
                        .fetch()
        );

        return lastAttempt;
    }

    private void setTutorCheckResults(AttemptDto lastAttempt) {
        lastAttempt.setTutorCheckResults(
                queryFactory.from(attemptCheckResult)
                        .innerJoin(attempt).on(attempt.id.eq(lastAttempt.getId()).and(attempt.autoCheckResults.any().id.eq(attemptCheckResult.id)))
                        .selectDto(AttemptCheckResultDto.class)
                        .fetch()
        );

        for (AttemptCheckResultDto tutorCheckResult : lastAttempt.getTutorCheckResults()) {
            tutorCheckResult.setFeatures(
                    queryFactory.from(feature)
                            .innerJoin(attemptCheckResult).on(attemptCheckResult.id.eq(tutorCheckResult.getId()).and(attemptCheckResult.features.any().id.eq(feature.id)))
                            .selectDto(FeatureDto.class)
                            .fetch()
            );
        }
    }

    private void setAutoCheckResults(AttemptDto lastAttempt) {
        lastAttempt.setAutoCheckResults(
                queryFactory.from(attemptCheckResult)
                        .innerJoin(attempt).on(attempt.id.eq(lastAttempt.getId()).and(attempt.autoCheckResults.any().id.eq(attemptCheckResult.id)))
                        .selectDto(AttemptCheckResultDto.class)
                        .fetch()
        );
        for (AttemptCheckResultDto autoCheckResult : lastAttempt.getAutoCheckResults()) {
            autoCheckResult.setFeatures(
                    queryFactory.from(feature)
                            .innerJoin(attemptCheckResult).on(attemptCheckResult.id.eq(autoCheckResult.getId()).and(attemptCheckResult.features.any().id.eq(feature.id)))
                            .selectDto(FeatureDto.class)
                            .fetch()
            );
        }
    }

    private JPAQuery<AttemptDto> baseQuery() {
        return queryFactory.from(attempt)
                .selectDto(AttemptDto.class);
    }
}
