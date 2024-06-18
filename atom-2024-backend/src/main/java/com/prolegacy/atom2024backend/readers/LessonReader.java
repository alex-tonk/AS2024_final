package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.query.query.DtoProjections;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.common.util.QueryUtils;
import com.prolegacy.atom2024backend.dto.*;
import com.prolegacy.atom2024backend.entities.*;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.querydsl.core.group.GroupBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(readOnly = true)
public class LessonReader {

    private static final QTopic topic = QTopic.topic;
    private static final QLesson topicLesson = new QLesson("topicLesson");
    private static final QLesson lesson = QLesson.lesson;
    private static final QTask lessonTask = new QTask("lessonTask");
    private static final QTask task = QTask.task;
    private static final QTrait lessonTrait = new QTrait("lessonTrait");
    private static final QTrait trait = QTrait.trait;
    private static final QSupplement lessonSupplement = new QSupplement("lessonSupplement");
    private static final QSupplement supplement = QSupplement.supplement;
    private static final QAttempt lastAttempt = new QAttempt("lastAttempt");

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserProvider userProvider;

    public List<LessonDto> getLessonsForTopic(TopicId topicId) {
        return this.getLessonsForTopic(topicId, false);
    }

    public List<LessonDto> getLessonsForTopic(TopicId topicId, boolean joinLastAttempt) {
        List<LessonDto> lessons = baseQuery()
                .where(topic.id.eq(topicId))
                .fetch();
        setTraits(lessons);
        if (joinLastAttempt) {
            setTasksWithLastAttempts(lessons, topicId);
        } else {
            setTasks(lessons);
        }
        setSupplements(lessons);
        return lessons;
    }

    private void setTraits(List<LessonDto> lessons) {
        if (lessons.isEmpty()) {
            return;
        }
        JPAQuery<?> traitsQuery = queryFactory
                .from(lesson)
                .innerJoin(lesson.traits, lessonTrait)
                .innerJoin(trait).on(lessonTrait.id.eq(trait.id))
                .where(QueryUtils.generateInExpression(lesson.id, lessons.stream().map(LessonDto::getId).toList()));
        Map<LessonId, List<TraitDto>> traitsByLesson = traitsQuery.transform(
                GroupBy.groupBy(lesson.id)
                        .as(GroupBy.list(DtoProjections.constructDto(traitsQuery, TraitDto.class, trait)))
        );
        lessons.forEach(
                lesson -> lesson.setTraits(traitsByLesson.getOrDefault(lesson.getId(), new ArrayList<>()))
        );
    }

    private void setTasks(List<LessonDto> lessons) {
        if (lessons.isEmpty()) {
            return;
        }
        JPAQuery<?> tasksQuery = queryFactory.from(topic)
                .innerJoin(topic.lessons, topicLesson)
                .innerJoin(lesson).on(lesson.id.eq(topicLesson.id))
                .innerJoin(lesson.tasks, lessonTask)
                .innerJoin(task).on(lessonTask.id.eq(task.id))
                .where(QueryUtils.generateInExpression(lesson.id, lessons.stream().map(LessonDto::getId).toList()));
        Map<LessonId, List<TaskDto>> tasksByLesson = tasksQuery.transform(
                GroupBy.groupBy(lesson.id)
                        .as(GroupBy.list(DtoProjections.constructDto(tasksQuery, TaskDto.class, task)))
        );
        lessons.forEach(
                lesson -> lesson.setTasks(tasksByLesson.getOrDefault(lesson.getId(), new ArrayList<>()))
        );
    }

    private void setTasksWithLastAttempts(List<LessonDto> lessons, TopicId topicId) {
        if (lessons.isEmpty()) {
            return;
        }
        JPAQuery<?> traitsQuery = queryFactory.from(topic)
                .innerJoin(topic.lessons, topicLesson)
                .innerJoin(lesson).on(lesson.id.eq(topicLesson.id))
                .innerJoin(lesson.tasks, lessonTask)
                .innerJoin(task).on(lessonTask.id.eq(task.id))
                .leftJoin(lastAttempt).on(
                        lastAttempt.topic.id.eq(topicId)
                                .and(lastAttempt.lesson.id.eq(lesson.id))
                                .and(lastAttempt.task.id.eq(task.id))
                                .and(lastAttempt.user.id.eq(userProvider.get().getId()))
                                .and(lastAttempt.isLastAttempt.isTrue())
                ).where(QueryUtils.generateInExpression(lesson.id, lessons.stream().map(LessonDto::getId).toList()));
        Map<LessonId, List<TaskDto>> tasksByLesson = traitsQuery.transform(
                GroupBy.groupBy(lesson.id)
                        .as(GroupBy.list(DtoProjections.constructDto(traitsQuery, TaskDto.class, task)))
        );
        lessons.forEach(
                lesson -> lesson.setTasks(tasksByLesson.getOrDefault(lesson.getId(), new ArrayList<>()))
        );
    }

    private void setSupplements(List<LessonDto> lessons) {
        if (lessons.isEmpty()) {
            return;
        }
        JPAQuery<?> supplementsQuery = queryFactory
                .from(lesson)
                .innerJoin(lesson.supplements, lessonSupplement)
                .innerJoin(supplement).on(lessonSupplement.id.eq(supplement.id))
                .where(QueryUtils.generateInExpression(lesson.id, lessons.stream().map(LessonDto::getId).toList()));;
        Map<LessonId, List<SupplementDto>> supplementsByLesson = supplementsQuery.transform(
                GroupBy.groupBy(lesson.id)
                        .as(GroupBy.list(DtoProjections.constructDto(supplementsQuery, SupplementDto.class, supplement)))
        );
        lessons.forEach(
                lesson -> lesson.setSupplements(supplementsByLesson.getOrDefault(lesson.getId(), new ArrayList<>()))
        );
    }

    private JPAQuery<LessonDto> baseQuery() {
        return queryFactory.from(topic)
                .innerJoin(topic.lessons, topicLesson)
                .innerJoin(lesson).on(lesson.id.eq(topicLesson.id))
                .selectDto(LessonDto.class, lesson);
    }
}
