package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.DtoProjections;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.common.util.QueryUtils;
import com.prolegacy.atom2024backend.dto.TopicDto;
import com.prolegacy.atom2024backend.dto.TraitDto;
import com.prolegacy.atom2024backend.entities.QLesson;
import com.prolegacy.atom2024backend.entities.QTopic;
import com.prolegacy.atom2024backend.entities.QTrait;
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
public class TopicReader {
    private static final QTopic topic = QTopic.topic;
    private static final QLesson lesson = QLesson.lesson;
    private static final QTrait topicTrait = new QTrait("topicTrait");
    private static final QTrait trait = QTrait.trait;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<TopicDto> getTopics() {
        List<TopicDto> topics = baseQuery().fetch();
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

    private JPAQuery<TopicDto> baseQuery() {
        return queryFactory.from(topic)
                .leftJoin(topic.lessons, lesson)
                .groupBy(topic)
                .selectDto(
                        TopicDto.class,
                        lesson.id.count().as("lessonsCount")
                );
    }

}
