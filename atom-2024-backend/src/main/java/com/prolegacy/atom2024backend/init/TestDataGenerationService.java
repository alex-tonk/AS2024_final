package com.prolegacy.atom2024backend.init;

import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.readers.RoleReader;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.auth.services.UserService;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import com.prolegacy.atom2024backend.dto.CourseDto;
import com.prolegacy.atom2024backend.dto.StudentDto;
import com.prolegacy.atom2024backend.dto.StudyGroupDto;
import com.prolegacy.atom2024backend.dto.TutorDto;
import com.prolegacy.atom2024backend.readers.CourseReader;
import com.prolegacy.atom2024backend.readers.StudentReader;
import com.prolegacy.atom2024backend.readers.StudyGroupReader;
import com.prolegacy.atom2024backend.readers.TutorReader;
import com.prolegacy.atom2024backend.services.CourseService;
import com.prolegacy.atom2024backend.services.StudentService;
import com.prolegacy.atom2024backend.services.StudyGroupService;
import com.prolegacy.atom2024backend.services.TutorService;
import org.apache.commons.codec.digest.DigestUtils;
import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 101)
public class TestDataGenerationService implements ApplicationRunner {
    @Autowired
    private RoleReader roleReader;

    @Autowired
    private UserService userService;

    @Autowired
    private UserReader userReader;
    @Autowired
    private StudentReader studentReader;
    @Autowired
    private TutorReader tutorReader;
    @Autowired
    private CourseReader courseReader;
    @Autowired
    private StudyGroupReader studyGroupReader;

    @Autowired
    private StudentService studentService;
    @Autowired
    private TutorService tutorService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudyGroupService studyGroupService;

    @Override
    public void run(ApplicationArguments args) {
        List<RoleDto> roles = roleReader.getRoles();

        Instancio.ofList(UserDto.class)
                .size(20)
                .generate(Select.allStrings(), gen -> gen.text().loremIpsum().words(1))
                .generate(Select.field(UserDto::getPhoneNumber), gen -> gen.text().pattern("+7-9#d#d-#d#d#d-#d#d-#d#d"))
                .generate(Select.field(UserDto::getEmail), gen -> gen.net().email())
                .ignore(Select.field(UserDto::getPassword))
                .set(Select.field(UserDto::getRoles), roles)
                .create()
                .stream()
                .peek(userDto -> userDto.setPassword(Base64.getEncoder().encodeToString(DigestUtils.sha256(userDto.getEmail()))))
                .forEach(userService::registerUser);

        List<UserDto> users = userReader.getUsers();

        for (int i = 1; i < users.size(); ++i) {
            if (i % 2 == 0) studentService.createStudent(new StudentDto(null, users.get(i)));
            else tutorService.createTutor(new TutorDto(null, users.get(i)));
        }

        Instancio.ofList(CourseDto.class)
                .size(5)
                .generate(Select.field(CourseDto::getName), gen -> gen.text().loremIpsum().words(4))
                .create()
                .forEach(courseService::createCourse);

        Instancio.ofList(StudyGroupDto.class)
                .size(2)
                .generate(Select.field(StudyGroupDto::getName), gen -> gen.text().loremIpsum().words(4))
                .create()
                .forEach(studyGroupService::createStudyGroup);

        List<StudentDto> students = studentReader.getStudents();
        List<TutorDto> tutors = tutorReader.getTutors();
        List<CourseDto> courses = courseReader.getCourses();
        List<StudyGroupDto> studyGroups = studyGroupReader.getStudyGroups();

        for (StudyGroupDto studyGroup : studyGroups) {
            ArrayList<StudentDto> studentDtos = new ArrayList<>(students);
            for (int i = 0; i < 5; ++i) {
                StudentDto randomStudent = Gen.oneOf(studentDtos).get();
                studentDtos.remove(randomStudent);
                studyGroupService.addStudent(studyGroup.getId(), randomStudent.getId());
            }

            ArrayList<CourseDto> courseDtos = new ArrayList<>(courses);
            for (int i = 0; i < 2; i++) {
                CourseDto randomCourse = Gen.oneOf(courseDtos).get();
                courseDtos.remove(randomCourse);
                studyGroupService.addCourse(studyGroup.getId(), randomCourse.getId());

                ArrayList<TutorDto> tutorDtos = new ArrayList<>(tutors);
                for (int j = 0; j < 3; j++) {
                    TutorDto randomTutor = Gen.oneOf(tutorDtos).get();
                    tutorDtos.remove(randomTutor);
                    studyGroupService.addTutor(studyGroup.getId(), randomCourse.getId(), randomTutor.getId());
                }
            }
        }
    }
}
