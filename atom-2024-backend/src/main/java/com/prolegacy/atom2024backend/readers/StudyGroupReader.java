package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.entities.QUser;
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
import com.prolegacy.atom2024backend.entities.*;
import com.prolegacy.atom2024backend.entities.ids.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class StudyGroupReader {
    private static final QStudyGroup studyGroup = QStudyGroup.studyGroup;
    private static final QStudentInGroup studentInGroup = QStudentInGroup.studentInGroup;
    private static final QStudent student = QStudent.student;
    private static final QUser student$user = new QUser("student$user");
    private static final QCourseWithTutors courseWithTutors = QCourseWithTutors.courseWithTutors;
    private static final QCourse course = QCourse.course;
    private static final QTutorInCourse tutorInCourse = QTutorInCourse.tutorInCourse;
    private static final QTutor tutor = QTutor.tutor;
    private static final QUser tutor$user = new QUser("tutor$user");

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

    public List<StudyGroupDto> getStudyGroupsForTutor(TutorId tutorId) {
        return baseQuery()
                .where(courseWithTutors.tutors.any().id.tutorId.eq(tutorId))
                .fetch();
    }

    public List<StudyGroupDto> getStudyGroupsForStudent(StudentId studentId) {
        QStudentInGroup siq = new QStudentInGroup("siq");
        return baseQuery()
                .innerJoin(studyGroup.students, siq)
                .where(siq.student.id.eq(studentId))
                .fetch();
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

    public List<CourseWithTutorsDto> getCoursesForTutor(StudyGroupId studyGroupId, TutorId tutorId) {
        return courseQuery()
                .where(courseWithTutors.studyGroup.id.eq(studyGroupId))
                .where(courseWithTutors.tutors.any().id.tutorId.eq(tutorId))
                .fetch();
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
        StringTemplate studentNames = Expressions.stringTemplate("function('stringAggDistinct', {0}, ', ')", UserReader.getFullName(student$user));
        StringTemplate courseNames = Expressions.stringTemplate("function('stringAggDistinct', {0}, ', ')", courseWithTutors.course.name);
        return queryFactory.from(studyGroup)
                .leftJoin(studentInGroup).on(studentInGroup.studyGroup.id.eq(studyGroup.id))
                .leftJoin(student).on(student.id.eq(studentInGroup.student.id))
                .leftJoin(student$user).on(student$user.id.eq(student.user.id))
                .leftJoin(courseWithTutors).on(courseWithTutors.studyGroup.id.eq(studyGroup.id))
                .leftJoin(course).on(course.id.eq(courseWithTutors.course.id))
                .orderBy(studyGroup.id.desc())
                .groupBy(studyGroup)
                .selectDto(
                        StudyGroupDto.class,
                        studentNames.as("studentNames"),
                        student.countDistinct().as("studentsCount"),
                        courseNames.as("courseNames"),
                        course.countDistinct().as("coursesCount")
                );
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
        StringTemplate tutorsNames = Expressions.stringTemplate("function('stringAgg', {0}, ', ')", UserReader.getFullName(tutor$user));
        return queryFactory.from(courseWithTutors)
                .leftJoin(studyGroup).on(studyGroup.id.eq(courseWithTutors.studyGroup.id))
                .leftJoin(course).on(course.id.eq(courseWithTutors.course.id))
                .leftJoin(tutorInCourse).on(tutorInCourse.courseWithTutors.id.eq(courseWithTutors.id))
                .leftJoin(tutor).on(tutor.id.eq(tutorInCourse.tutor.id))
                .leftJoin(tutor$user).on(tutor$user.id.eq(tutor.user.id))
                .groupBy(studyGroup, course)
                .selectDto(
                        CourseWithTutorsDto.class,
                        tutorsNames.as("tutorNames"),
                        tutor.count().as("tutorsCount")
                );
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
