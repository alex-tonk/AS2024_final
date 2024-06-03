package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.query.lazy.PageHelper;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.TutorDto;
import com.prolegacy.atom2024backend.entities.QTutor;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class TutorReader {
    private static final QTutor tutor = QTutor.tutor;

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private PageHelper pageHelper;

    public TutorDto getTutor(TutorId id) {
        return baseQuery()
                .where(tutor.id.eq(id))
                .fetchFirst();
    }

    public List<TutorDto> getTutors() {
        return baseQuery().fetch();
    }

    public PageResponse<TutorDto> searchTutors(PageQuery pageQuery) {
        return pageHelper.paginate(baseQuery(), pageQuery);
    }

    private JPAQuery<TutorDto> baseQuery() {
        return queryFactory.from(tutor)
                .orderBy(tutor.id.desc())
                .selectDto(
                        TutorDto.class,
                        UserReader.getShortName(tutor.user).as("user$shortName"),
                        UserReader.getFullName(tutor.user).as("user$fullName")
                );
    }
}
