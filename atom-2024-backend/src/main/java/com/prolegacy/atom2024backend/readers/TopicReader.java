package com.prolegacy.atom2024backend.readers;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class TopicReader {
//    private static final QCourse course = QCourse.course;
//    private static final QModule module = QModule.module;
//
//    @Autowired
//    private JPAQueryFactory queryFactory;
//    @Autowired
//    private PageHelper pageHelper;
//
//    public TopicDto getCourse(TopicId id) {
//        TopicDto topicDto = baseQuery()
//                .where(course.id.eq(id))
//                .fetchFirst();
//
//        if (topicDto != null) {
//            topicDto.setModules(modulesQuery().where(module.course.id.eq(id)).fetch());
//        }
//
//        return topicDto;
//    }
//
//    public List<TopicDto> getCourses() {
//        return baseQuery().fetch();
//    }
//
//    public PageResponse<TopicDto> searchCourses(PageQuery pageQuery) {
//        return pageHelper.paginate(baseQuery(), pageQuery);
//    }
//
//    private JPAQuery<TopicDto> baseQuery() {
//        return queryFactory.from(course)
//                .orderBy(course.id.desc())
//                .selectDto(TopicDto.class);
//    }
//
//    private JPAQuery<ModuleDto> modulesQuery() {
//        return queryFactory.from(module).selectDto(ModuleDto.class);
//    }
}
