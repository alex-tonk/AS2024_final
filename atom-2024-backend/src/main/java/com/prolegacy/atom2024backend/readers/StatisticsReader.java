package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.hibernate.LongId;
import com.prolegacy.atom2024backend.dto.StudentRankingDto;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class StatisticsReader {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<StudentRankingDto> getStudentRankings(Boolean onSum,
                                                      Optional<TopicId> topicId) {
        if (onSum) {
            return jdbcTemplate.queryForStream("""
                                                
                                    with data as (select u.firstname || coalesce(' ' || u.surname || ' ', ' ') || u.lastname as "fullName",
                                         sum(extract(epoch from a.end_date - a.start_date))                 as "totalCompleteTimeSeconds",
                                         avg(extract(epoch from a.end_date - a.start_date))                 as "completeTimeSeconds",
                                         sum(case
                                                 when a.tutor_mark = 'EXCELLENT' then 5
                                                 when a.tutor_mark = 'GOOD' then 4
                                                 when a.tutor_mark = 'MEDIOCRE' then 3
                                                 when a.tutor_mark = 'FAILED' then 2 end)                   as "totalMark",
                                         avg(case
                                                 when a.tutor_mark = 'EXCELLENT' then 5
                                                 when a.tutor_mark = 'GOOD' then 4
                                                 when a.tutor_mark = 'MEDIOCRE' then 3
                                                 when a.tutor_mark = 'FAILED' then 2 end)                   as "mark",
                                         count(a.id) as "completeTaskCount"
                                  from "user" u
                                           inner join attempt a on a.user_id = u.id
                                      and case
                                              when a.tutor_mark = 'EXCELLENT' then 5
                                              when a.tutor_mark = 'GOOD' then 4
                                              when a.tutor_mark = 'MEDIOCRE' then 3
                                              when a.tutor_mark = 'FAILED' then 2 end >= 3
                                      and a.is_last_attempt = true
                                           inner join topic t on t.id = a.topic_id
                                           inner join lesson l on l.id = a.lesson_id
                                  where case when ? is not null then topic_id = ? else true end
                                  group by u.id, u.firstname || coalesce(' ' || u.surname || ' ', ' ') || u.lastname)
                                                
                            select rank() over (order by "totalMark" desc nulls last, "totalCompleteTimeSeconds" nulls last) as "rank",
                                   "fullName" as "fullName",
                                   round("totalCompleteTimeSeconds") as "totalCompleteTimeSeconds",
                                   round("completeTimeSeconds") as "completeTimeSeconds",
                                   "totalMark" as "totalMark",
                                   round("mark", 2) as "mark",
                                   "completeTaskCount" as "completeTaskCount"
                            from data
                            order by rank() over (order by "totalMark" desc nulls last, "totalCompleteTimeSeconds" nulls last);
                            """,
                    ps -> topicId.ifPresentOrElse(
                            id -> {
                                try {
                                    ps.setLong(1, id.longValue());
                                    ps.setLong(2, id.longValue());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> {
                                try {
                                    ps.setNull(1, SqlTypes.BIGINT);
                                    ps.setNull(2, SqlTypes.BIGINT);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ),
                    (rs, rowNum) -> new StudentRankingDto(
                            rs.getString("fullName"),
                            rs.getLong("rank"),
                            rs.getDouble("mark"),
                            rs.getLong("totalMark"),
                            rs.getLong("totalCompleteTimeSeconds"),
                            rs.getLong("completeTimeSeconds"),
                            rs.getLong("completeTaskCount")
                    )
            ).toList();
        } else {
            return jdbcTemplate.queryForStream("""
                                                
                                    with data as (select u.firstname || coalesce(' ' || u.surname || ' ', ' ') || u.lastname as "fullName",
                                         sum(extract(epoch from a.end_date - a.start_date))                 as "totalCompleteTimeSeconds",
                                         avg(extract(epoch from a.end_date - a.start_date))                 as "completeTimeSeconds",
                                         sum(case
                                                 when a.tutor_mark = 'EXCELLENT' then 5
                                                 when a.tutor_mark = 'GOOD' then 4
                                                 when a.tutor_mark = 'MEDIOCRE' then 3
                                                 when a.tutor_mark = 'FAILED' then 2 end)                   as "totalMark",
                                         avg(case
                                                 when a.tutor_mark = 'EXCELLENT' then 5
                                                 when a.tutor_mark = 'GOOD' then 4
                                                 when a.tutor_mark = 'MEDIOCRE' then 3
                                                 when a.tutor_mark = 'FAILED' then 2 end)                   as "mark",
                                         count(a.id) as "completeTaskCount"
                                  from "user" u
                                           inner join attempt a on a.user_id = u.id
                                      and case
                                              when a.tutor_mark = 'EXCELLENT' then 5
                                              when a.tutor_mark = 'GOOD' then 4
                                              when a.tutor_mark = 'MEDIOCRE' then 3
                                              when a.tutor_mark = 'FAILED' then 2 end >= 3
                                      and a.is_last_attempt = true
                                           inner join topic t on t.id = a.topic_id
                                           inner join lesson l on l.id = a.lesson_id
                                  where case when ? is not null then topic_id = ? else true end
                                  group by u.id, u.firstname || coalesce(' ' || u.surname || ' ', ' ') || u.lastname)
                                                
                            select rank() over (order by "mark" desc nulls last, "completeTimeSeconds" nulls last) as "rank",
                                   "fullName" as "fullName",
                                   round("totalCompleteTimeSeconds") as "totalCompleteTimeSeconds",
                                   round("completeTimeSeconds") as "completeTimeSeconds",
                                   "totalMark" as "totalMark",
                                   round("mark", 2) as "mark",
                                   "completeTaskCount" as "completeTaskCount"
                            from data
                            order by rank() over (order by "mark" desc nulls last, "completeTimeSeconds" nulls last);
                            """,
                    ps -> topicId.ifPresentOrElse(
                            id -> {
                                try {
                                    ps.setLong(1, id.longValue());
                                    ps.setLong(2, id.longValue());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> {
                                try {
                                    ps.setNull(1, SqlTypes.BIGINT);
                                    ps.setNull(2, SqlTypes.BIGINT);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ),
                    (rs, rowNum) -> new StudentRankingDto(
                            rs.getString("fullName"),
                            rs.getLong("rank"),
                            rs.getDouble("mark"),
                            rs.getLong("totalMark"),
                            rs.getLong("totalCompleteTimeSeconds"),
                            rs.getLong("completeTimeSeconds"),
                            rs.getLong("completeTaskCount")
                    )).toList();
        }
    }
}
