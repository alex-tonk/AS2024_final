package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.query.lazy.PageHelper;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.CourseWithTutorsDto;
import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.TutorInCourseDto;
import com.prolegacy.atom2024backend.entities.QCourseWithTutors;
import com.prolegacy.atom2024backend.entities.QStudentInGroup;
import com.prolegacy.atom2024backend.entities.QStudyGroup;
import com.prolegacy.atom2024backend.entities.QTutorInCourse;
import com.prolegacy.atom2024backend.entities.ids.CourseWithTutorsId;
import com.prolegacy.atom2024backend.entities.ids.StudentInGroupId;
import com.prolegacy.atom2024backend.entities.ids.StudyGroupId;
import com.prolegacy.atom2024backend.entities.ids.TutorInCourseId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StudyGroupReader {
    private static final QStudyGroup studyGroup = QStudyGroup.studyGroup;
    private static final QStudentInGroup studentInGroup = QStudentInGroup.studentInGroup;
    private static final QCourseWithTutors courseWithTutors = QCourseWithTutors.courseWithTutors;
    private static final QTutorInCourse tutorInCourse = QTutorInCourse.tutorInCourse;

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

    public CourseWithTutorsDto getCourse(CourseWithTutorsId courseWithTutorsId) {
        return courseQuery().where(courseWithTutors.id.eq(courseWithTutorsId)).fetchFirst();
    }

    public List<CourseWithTutorsDto> getCourses(StudyGroupId studyGroupId) {
        return courseQuery().where(courseWithTutors.id.studyGroupId.eq(studyGroupId)).fetch();
    }

    public PageResponse<CourseWithTutorsDto> searchCourses(StudyGroupId studyGroupId, PageQuery pageQuery) {
        return pageHelper.paginate(courseQuery().where(courseWithTutors.id.studyGroupId.eq(studyGroupId)), pageQuery);
    }

    public TutorInCourseDto getTutor(TutorInCourseId tutorInCourseId) {
        return tutorQuery().where(tutorInCourse.id.eq(tutorInCourseId)).fetchFirst();
    }

    public List<TutorInCourseDto> getTutors(CourseWithTutorsId courseWithTutorsId) {
        return tutorQuery().where(tutorInCourse.id.courseWithTutorsId.eq(courseWithTutorsId)).fetch();
    }

    public PageResponse<TutorInCourseDto> searchTutors(CourseWithTutorsId courseWithTutorsId, PageQuery pageQuery) {
        return pageHelper.paginate(tutorQuery().where(tutorInCourse.id.courseWithTutorsId.eq(courseWithTutorsId)), pageQuery);
    }

    private JPAQuery<StudyGroupDto> baseQuery() {
        return queryFactory.from(studyGroup).selectDto(StudyGroupDto.class);
    }

    private JPAQuery<StudentInGroupDto> studentQuery() {
        return queryFactory.from(studentInGroup)
                .selectDto(
                        StudentInGroupDto.class,
                        UserReader.getFullName(studentInGroup.student.user).as("student$user$fullName"),
                        UserReader.getShortName(studentInGroup.student.user).as("student$user$shortName")
                );
    }

    private JPAQuery<CourseWithTutorsDto> courseQuery() {
        return queryFactory.from(courseWithTutors).selectDto(CourseWithTutorsDto.class);
    }

    private JPAQuery<TutorInCourseDto> tutorQuery() {
        return queryFactory.from(tutorInCourse)
                .selectDto(
                        TutorInCourseDto.class,
                        UserReader.getFullName(tutorInCourse.tutor.user).as("tutor$user$fullName"),
                        UserReader.getShortName(tutorInCourse.tutor.user).as("tutor$user$shortName")
                );
    }
}
