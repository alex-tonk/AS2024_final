package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.FileLessonDto;
import com.prolegacy.atom2024backend.entities.enums.LessonType;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.survey.entities.Survey;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private LessonId id;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @Enumerated(EnumType.STRING)
    private LessonType type;

    @Column(columnDefinition = "text")
    private String name;
    private Long orderNumber;

    @OneToOne(orphanRemoval = true)
    private Survey survey;

//    @OneToOne(orphanRemoval = true)
//    private Lecture lecture;

    @OneToOne(orphanRemoval = true)
    private File file;

    @OneToOne(orphanRemoval = true)
    private Workshop workshop;

    @OneToOne(orphanRemoval = true)
    private Essay essay;

    private Lesson(String name) {
        this.name = name;
    }

    public static Lesson createSurveyLesson(Module module, Survey survey, String name) {
        Lesson lesson = new Lesson(name);
        lesson.module = module;
        lesson.survey = survey;
        lesson.type = LessonType.SURVEY;
        return lesson;
    }

//    public static Lesson createLectureLesson(Module module, Lecture lecture, String name) {
//        Lesson lesson = new Lesson(name);
//        lesson.module = module;
//        lesson.lecture = lecture;
//        lesson.type = LessonType.LECTURE;
//        return lesson;
//    }

    public static Lesson createFileLesson(Module module, File file, String name) {
        Lesson lesson = new Lesson(name);
        lesson.module = module;
        lesson.file = file;
        lesson.type = LessonType.FILE;
        return lesson;
    }

    public static Lesson createWorkshopLesson(Module module, Workshop workshop, String name) {
        Lesson lesson = new Lesson(name);
        lesson.module = module;
        lesson.workshop = workshop;
        lesson.type = LessonType.WORKSHOP;
        return lesson;
    }

    public static Lesson createEssayLesson(Module module, Essay essay, String name) {
        Lesson lesson = new Lesson(name);
        lesson.module = module;
        lesson.essay = essay;
        lesson.type = LessonType.ESSAY;
        return lesson;
    }

}
