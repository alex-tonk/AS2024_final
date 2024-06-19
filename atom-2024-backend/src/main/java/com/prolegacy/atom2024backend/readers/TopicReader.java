package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.QUser;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.query.query.DtoProjections;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.common.util.QueryUtils;
import com.prolegacy.atom2024backend.dto.LessonDto;
import com.prolegacy.atom2024backend.dto.TopicDto;
import com.prolegacy.atom2024backend.dto.TraitDto;
import com.prolegacy.atom2024backend.entities.*;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.enums.Mark;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(readOnly = true)
public class TopicReader {
    private static final QTopic topic = QTopic.topic;
    private static final QLesson topicLesson = new QLesson("topicLesson");
    private static final QLesson lesson = QLesson.lesson;
    private static final QTask task = QTask.task;
    private static final QAttempt lastAttempt = new QAttempt("lastAttempt");
    private static final QTrait topicTrait = new QTrait("topicTrait");
    private static final QTrait trait = QTrait.trait;
    private static final QUser user = QUser.user;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private LessonReader lessonReader;

    @Autowired
    private UserProvider userProvider;

    public List<UserDto> getUsersWithFinishedTopic(TopicId topicId) {
        return queryFactory.from(user)
                .innerJoin(lastAttempt).on(
                        lastAttempt.user.id.eq(user.id)
                                .and(lastAttempt.isLastAttempt.isTrue())
                                .and(lastAttempt.tutorMark.in(List.of(Mark.EXCELLENT, Mark.GOOD, Mark.MEDIOCRE)))
                ).leftJoin(topic).on(topic.id.eq(lastAttempt.topic.id))
                .leftJoin(lesson).on(topic.lessons.any().id.eq(lesson.id))
                .leftJoin(task).on(lesson.tasks.any().id.eq(task.id))
                .where(topic.id.eq(topicId))
                .groupBy(user, topic)
                .having(lastAttempt.id.countDistinct().eq(lesson.id.stringValue().concat("/").concat(task.id.stringValue()).countDistinct()))
                .selectDto(UserDto.class, UserReader.getFullName(user).as("fullName"))
                .fetch();
    }

    public List<TopicDto> getTopics() {
        List<TopicDto> topics = baseQuery().fetch();
        setLessons(topics);
        setTraits(topics);
        return topics;
    }

    private void setTraits(List<TopicDto> topics) {
        if (topics.isEmpty()) {
            return;
        }
        JPAQuery<?> traitsQuery = queryFactory
                .from(topic)
                .innerJoin(topic.traits, topicTrait)
                .innerJoin(trait).on(topicTrait.id.eq(trait.id))
                .where(QueryUtils.generateInExpression(topic.id, topics.stream().map(TopicDto::getId).toList()));
        Map<TopicId, List<TraitDto>> traitsByTopic = traitsQuery.transform(
                GroupBy.groupBy(topic.id)
                        .as(GroupBy.list(DtoProjections.constructDto(traitsQuery, TraitDto.class, trait)))
        );
        topics.forEach(
                topic -> topic.setTraits(traitsByTopic.getOrDefault(topic.getId(), new ArrayList<>()))
        );
    }

    private void setLessons(List<TopicDto> topics) {
        if (topics.isEmpty()) {
            return;
        }

        JPAQuery<?> traitsQuery = queryFactory.from(topic)
                .innerJoin(topic.lessons, topicLesson)
                .innerJoin(lesson).on(lesson.id.eq(topicLesson.id))
                .where(QueryUtils.generateInExpression(topic.id, topics.stream().map(TopicDto::getId).toList()));
        Map<TopicId, List<LessonDto>> lessonByTopic = traitsQuery.transform(
                GroupBy.groupBy(topic.id)
                        .as(GroupBy.list(DtoProjections.constructDto(traitsQuery, LessonDto.class, lesson)))
        );
        List<LessonDto> allLessons = lessonByTopic.values().stream().flatMap(Collection::stream).toList();
        lessonReader.setTasks(allLessons);
        lessonReader.setTraits(allLessons);
        lessonReader.setSupplements(allLessons);

        topics.forEach(
                topicDto -> topicDto.setLessons(lessonByTopic.getOrDefault(topicDto.getId(), new ArrayList<>()))
        );
    }

    private JPAQuery<TopicDto> baseQuery() {

        return queryFactory.from(topic)
                .leftJoin(topic.lessons, lesson)
                .leftJoin(lesson.tasks, task)
                .leftJoin(lastAttempt).on(
                        lastAttempt.topic.id.eq(topic.id)
                                .and(lastAttempt.lesson.id.eq(lesson.id))
                                .and(lastAttempt.task.id.eq(task.id))
                                .and(lastAttempt.user.id.eq(userProvider.get().getId()))
                                .and(lastAttempt.isLastAttempt.isTrue())
                                .and(lastAttempt.tutorMark.in(List.of(Mark.EXCELLENT, Mark.GOOD, Mark.MEDIOCRE)))
                )
                .groupBy(topic)
                .selectDto(
                        TopicDto.class,
                        task.id.count().as("taskCount"),
                        lastAttempt.id.count().as("taskPassedCount")
                );
    }

    private static NumberExpression<BigDecimal> markToValue(EnumPath<Mark> mark) {
        return Expressions.cases()
                .when(mark.eq(Mark.FAILED)).then(BigDecimal.valueOf(2))
                .when(mark.eq(Mark.MEDIOCRE)).then(BigDecimal.valueOf(3))
                .when(mark.eq(Mark.GOOD)).then(BigDecimal.valueOf(4))
                .when(mark.eq(Mark.EXCELLENT)).then(BigDecimal.valueOf(5))
                .otherwise(BigDecimal.valueOf(0));
    }

}
