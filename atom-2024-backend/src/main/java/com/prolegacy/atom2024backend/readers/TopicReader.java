package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.TopicDto;
import com.prolegacy.atom2024backend.entities.QLesson;
import com.prolegacy.atom2024backend.entities.QTopic;
import com.querydsl.core.types.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class TopicReader {
    private static final QTopic topic = QTopic.topic;
    private static final QLesson lesson = QLesson.lesson;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<TopicDto> getTopics() {
        return baseQuery().fetch();
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
