package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.TaskDto;
import com.prolegacy.atom2024backend.entities.QAttempt;
import com.prolegacy.atom2024backend.entities.QTask;
import com.prolegacy.atom2024backend.enums.AttemptStatus;
import com.prolegacy.atom2024backend.enums.Mark;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class TaskReader {
    private static final QTask task = QTask.task;
    private static final QAttempt attempt = QAttempt.attempt;

    @Autowired
    private JPAQueryFactory queryFactory;


    public List<TaskDto> getTasks() {
        return baseQuery().fetch();
    }

    private JPAQuery<TaskDto> baseQuery() {
        NumberExpression<BigDecimal> NAS = Expressions.asNumber(BigDecimal.valueOf(5)).subtract(markToValue(attempt.tutorMark).avg().castToNum(BigDecimal.class)).divide(BigDecimal.valueOf(3));
        NumberExpression<BigDecimal> subtract = attempt.count().castToNum(BigDecimal.class).subtract(BigDecimal.ONE);
        NumberExpression<BigDecimal> NNA = Expressions.cases().when(subtract.gt(BigDecimal.ZERO)).then(subtract).otherwise(Expressions.asNumber(BigDecimal.ZERO))
                .divide(Expressions.numberTemplate(BigDecimal.class, "function('maxNumberOfAttempts')").subtract(BigDecimal.ONE).coalesce(BigDecimal.ONE));
        NumberExpression<BigDecimal> NTL = Expressions.asNumber(BigDecimal.ONE).subtract(task.time.castToNum(BigDecimal.class).divide(queryFactory.from(task).select(task.time.max())));
        NumberExpression<BigDecimal> NAT = Expressions.numberTemplate(BigDecimal.class, "function('getDifferenceSeconds', {0}, {1})", attempt.endDate, attempt.startDate).castToNum(Integer.class).coalesce(0).avg().divide(BigDecimal.valueOf(60)).divide(task.time).castToNum(BigDecimal.class);

        NumberExpression<BigDecimal> DS = Expressions.asNumber(BigDecimal.ONE).add(NAS.add(NNA).add(NTL).add(NAT));

        return queryFactory.from(task)
                .leftJoin(attempt).on(attempt.task.id.eq(task.id).and(attempt.status.eq(AttemptStatus.DONE)))
                .groupBy(task)
                .selectDto(TaskDto.class, DS.as("difficultyScore"));
    }

    private static NumberExpression<BigDecimal> markToValue(EnumPath<Mark> mark) {
        return Expressions.cases()
                .when(mark.eq(Mark.FAILED)).then(BigDecimal.valueOf(2))
                .when(mark.eq(Mark.MEDIOCRE)).then(BigDecimal.valueOf(3))
                .when(mark.eq(Mark.GOOD)).then(BigDecimal.valueOf(4))
                .when(mark.eq(Mark.EXCELLENT)).then(BigDecimal.valueOf(5))
                .otherwise(BigDecimal.valueOf(5));
    }
}
