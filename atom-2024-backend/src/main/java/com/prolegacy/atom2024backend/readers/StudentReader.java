package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.lazy.PageHelper;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.StudentDto;
import com.prolegacy.atom2024backend.entities.QStudent;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StudentReader {
    private static final QStudent student = QStudent.student;

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private PageHelper pageHelper;

    public StudentDto getStudent(StudentId id) {
        return baseQuery()
                .where(student.id.eq(id))
                .fetchFirst();
    }

    public List<StudentDto> getStudents() {
        return baseQuery().fetch();
    }

    public PageResponse<StudentDto> searchStudents(PageQuery pageQuery) {
        return pageHelper.paginate(baseQuery(), pageQuery);
    }

    private JPAQuery<StudentDto> baseQuery() {
        return queryFactory.from(student)
                .orderBy(student.id.desc())
                .selectDto(StudentDto.class);
    }
}
