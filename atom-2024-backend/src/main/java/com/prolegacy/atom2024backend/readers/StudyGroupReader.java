package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.lazy.PageHelper;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.TutorWithCourseDto;
import com.prolegacy.atom2024backend.entities.QStudentInGroup;
import com.prolegacy.atom2024backend.entities.QStudyGroup;
import com.prolegacy.atom2024backend.entities.QTutorWithCourse;
import com.prolegacy.atom2024backend.entities.ids.StudentInGroupId;
import com.prolegacy.atom2024backend.entities.ids.StudyGroupId;
import com.prolegacy.atom2024backend.entities.ids.TutorWithCourseId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StudyGroupReader {
    private static final QStudyGroup studyGroup = QStudyGroup.studyGroup;
    private static final QStudentInGroup studentInGroup = QStudentInGroup.studentInGroup;
    private static final QTutorWithCourse tutorWithCourse = QTutorWithCourse.tutorWithCourse;

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private PageHelper pageHelper;

    public StudyGroupDto getStudyGroup(StudyGroupId studyGroupId) {
        return baseQuery().where(studyGroup.id.eq(studyGroupId)).fetchFirst();
    }

    public StudentInGroupDto getStudent(StudentInGroupId studentInGroupId) {
        return studentQuery()
                .where(studentInGroup.id.studyGroupId.eq(studentInGroupId.getStudyGroupId()))
                .where(studentInGroup.id.studentId.eq(studentInGroupId.getStudentId()))
                .fetchFirst();
    }

    public List<StudentInGroupDto> getStudents(StudyGroupId studyGroupId) {
        return studentQuery().where(studentInGroup.id.studyGroupId.eq(studyGroupId)).fetch();
    }

    public PageResponse<StudentInGroupDto> searchStudents(StudyGroupId studyGroupId, PageQuery pageQuery) {
        return pageHelper.paginate(studentQuery().where(studentInGroup.id.studyGroupId.eq(studyGroupId)), pageQuery);
    }

    public List<StudyGroupDto> getStudyGroups() {
        return baseQuery().fetch();
    }

    public PageResponse<StudyGroupDto> searchStudyGroups(PageQuery pageQuery) {
        return pageHelper.paginate(baseQuery(), pageQuery);
    }

    public TutorWithCourseDto getTutor(TutorWithCourseId tutorWithCourseId) {
        return tutorQuery()
                .where(tutorWithCourse.id.studyGroupId.eq(tutorWithCourseId.getStudyGroupId()))
                .where(tutorWithCourse.id.tutorId.eq(tutorWithCourseId.getTutorId()))
                .where(tutorWithCourse.id.courseId.eq(tutorWithCourseId.getCourseId()))
                .fetchFirst();
    }

    public List<TutorWithCourseDto> getTutors(StudyGroupId studyGroupId) {
        return tutorQuery()
                .where(tutorWithCourse.id.studyGroupId.eq(studyGroupId))
                .fetch();
    }

    public PageResponse<TutorWithCourseDto> searchTutors(StudyGroupId studyGroupId, PageQuery pageQuery) {
        return pageHelper.paginate(tutorQuery().where(tutorWithCourse.id.studyGroupId.eq(studyGroupId)), pageQuery);
    }

    private JPAQuery<StudyGroupDto> baseQuery() {
        return queryFactory.from(studyGroup)
                .selectDto(StudyGroupDto.class);
    }

    private JPAQuery<StudentInGroupDto> studentQuery() {
        return queryFactory.from(studentInGroup)
                .selectDto(StudentInGroupDto.class);
    }

    private JPAQuery<TutorWithCourseDto> tutorQuery() {
        return queryFactory.from(tutorWithCourse)
                .selectDto(TutorWithCourseDto.class);
    }
}
