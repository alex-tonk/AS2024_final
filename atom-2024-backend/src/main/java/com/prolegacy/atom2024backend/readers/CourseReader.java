package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.lazy.PageHelper;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.CourseDto;
import com.prolegacy.atom2024backend.dto.ModuleDto;
import com.prolegacy.atom2024backend.entities.QCourse;
import com.prolegacy.atom2024backend.entities.QModule;
import com.prolegacy.atom2024backend.entities.ids.CourseId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CourseReader {
    private static final QCourse course = QCourse.course;
    private static final QModule module = QModule.module;

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private PageHelper pageHelper;

    public CourseDto getCourse(CourseId id) {
        CourseDto courseDto = baseQuery()
                .where(course.id.eq(id))
                .fetchFirst();

        if (courseDto != null) {
            courseDto.setModules(modulesQuery().where(module.course.id.eq(id)).fetch());
        }

        return courseDto;
    }

    public List<CourseDto> getCourses() {
        return baseQuery().fetch();
    }

    public PageResponse<CourseDto> searchCourses(PageQuery pageQuery) {
        return pageHelper.paginate(baseQuery(), pageQuery);
    }

    private JPAQuery<CourseDto> baseQuery() {
        return queryFactory.from(course)
                .orderBy(course.id.desc())
                .selectDto(CourseDto.class);
    }

    private JPAQuery<ModuleDto> modulesQuery() {
        return queryFactory.from(module).selectDto(ModuleDto.class);
    }
}
