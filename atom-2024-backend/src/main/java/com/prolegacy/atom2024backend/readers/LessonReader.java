package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.DtoProjections;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.LessonDto;
import com.prolegacy.atom2024backend.dto.TaskDto;
import com.prolegacy.atom2024backend.dto.TopicDto;
import com.prolegacy.atom2024backend.dto.TraitDto;
import com.prolegacy.atom2024backend.entities.QLesson;
import com.prolegacy.atom2024backend.entities.QTask;
import com.prolegacy.atom2024backend.entities.QTopic;
import com.prolegacy.atom2024backend.entities.QTrait;
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

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<LessonDto> getLessonsForTopic(TopicId topicId) {
        List<LessonDto> lessons = baseQuery()
                .where(topic.id.eq(topicId))
                .fetch();
        setTraits(lessons);
        setTasks(lessons);
        return lessons;
    }

    private void setTraits(List<LessonDto> lessons) {
        if (lessons.isEmpty()) {
            return;
        }
        JPAQuery<?> traitsQuery = queryFactory
                .from(lesson)
                .innerJoin(lesson.traits, lessonTrait)
                .innerJoin(trait).on(lessonTrait.id.eq(trait.id));
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
        JPAQuery<?> traitsQuery = queryFactory
                .from(lesson)
                .innerJoin(lesson.tasks, lessonTask)
                .innerJoin(task).on(lessonTask.id.eq(task.id));
        Map<LessonId, List<TaskDto>> tasksByLesson = traitsQuery.transform(
                GroupBy.groupBy(lesson.id)
                        .as(GroupBy.list(DtoProjections.constructDto(traitsQuery, TaskDto.class, task)))
        );
        lessons.forEach(
                lesson -> lesson.setTasks(tasksByLesson.getOrDefault(lesson.getId(), new ArrayList<>()))
        );
    }

    private JPAQuery<LessonDto> baseQuery() {
        return queryFactory.from(topic)
                .innerJoin(topic.lessons, topicLesson)
                .innerJoin(lesson).on(lesson.id.eq(topicLesson.id))
                .selectDto(LessonDto.class, lesson);
    }
}
