package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.AttemptCheckResultDto;
import com.prolegacy.atom2024backend.dto.FeatureDto;
import com.prolegacy.atom2024backend.entities.*;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.LessonId;
import com.prolegacy.atom2024backend.entities.ids.TaskId;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.enums.AttemptStatus;
import com.prolegacy.atom2024backend.enums.Mark;
import com.prolegacy.atom2024backend.exceptions.TopicNotFoundException;
import com.prolegacy.atom2024backend.repositories.AttemptRepository;
import com.prolegacy.atom2024backend.repositories.FeatureRepository;
import com.prolegacy.atom2024backend.repositories.FileRepository;
import com.prolegacy.atom2024backend.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AttemptService {
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private FileRepository fileRepository;


    public void startNewAttempt(TopicId topicId, LessonId lessonId, TaskId taskId) {
        User user = userProvider.get();
        Topic topic = topicRepository.findById(topicId).orElseThrow(TopicNotFoundException::new);
        Lesson lesson = topic.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst().orElseThrow(() -> new BusinessLogicException("Данного урока нет в теме %s".formatted(topic.getTitle())));
        Task task = lesson.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst().orElseThrow(() -> new BusinessLogicException("Данного задания нет в теме %s в уроке %s".formatted(topic.getTitle(), lesson.getTitle())));

        Optional<Attempt> lastAttempt = attemptRepository.getByTopicAndLessonAndTaskAndUserAndIsLastAttemptTrue(
                topic, lesson, task, user
        );
        Boolean newAttemptAllowed = lastAttempt
                .map(a -> !(AttemptStatus.DONE.equals(a.getStatus())
                                && (a.getIsNewTryAllowed() || Mark.FAILED.equals(a.getTutorMark()))
                        )
                )
                .orElse(true);
        if (!newAttemptAllowed) {
            throw new BusinessLogicException("Вы не можете повторно пройти данное задание");
        }

        attemptRepository.save(new Attempt(topic, lesson, task, user, Instant.now(), lastAttempt));
    }

    public void finishAttempt(TopicId topicId, LessonId lessonId, TaskId taskId, List<FileId> fileIds) {
        User user = userProvider.get();
        Topic topic = topicRepository.findById(topicId).orElseThrow(TopicNotFoundException::new);
        Lesson lesson = topic.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst().orElseThrow(() -> new BusinessLogicException("Данного урока нет в теме %s".formatted(topic.getTitle())));
        Task task = lesson.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst().orElseThrow(() -> new BusinessLogicException("Данного задания нет в теме %s в уроке %s".formatted(topic.getTitle(), lesson.getTitle())));

        Attempt lastAttempt = attemptRepository.getByTopicAndLessonAndTaskAndUserAndIsLastAttemptTrue(
                topic, lesson, task, user
        ).orElseThrow(() -> new BusinessLogicException("Задание не было взято в работу"));

        if (!lastAttempt.getStatus().equals(AttemptStatus.IN_PROGRESS))
            throw new BusinessLogicException("Закончить можно только задания, находящиеся в работе");

        lastAttempt.finish(Instant.now(), fileRepository.findAllById(fileIds));
        // TODO: начать пинать машину
    }

    public void setTutorMark(
            TopicId topicId,
            LessonId lessonId,
            TaskId taskId,
            UserId userId,
            Mark mark,
            List<AttemptCheckResultDto> checkResults,
            String comment
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException("Проверяемого обучаешегося не существует"));
        Topic topic = topicRepository.findById(topicId).orElseThrow(TopicNotFoundException::new);
        Lesson lesson = topic.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst().orElseThrow(() -> new BusinessLogicException("Данного урока нет в теме %s".formatted(topic.getTitle())));
        Task task = lesson.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst().orElseThrow(() -> new BusinessLogicException("Данного задания нет в теме %s в уроке %s".formatted(topic.getTitle(), lesson.getTitle())));

        Attempt lastAttempt = attemptRepository.getByTopicAndLessonAndTaskAndUserAndIsLastAttemptTrue(
                topic, lesson, task, user
        ).orElseThrow(() -> new BusinessLogicException("Задание не было взято в работу"));

        if (AttemptStatus.IN_PROGRESS.equals(lastAttempt.getStatus()))
            throw new BusinessLogicException("Задание не было отправлено на проверку");

        lastAttempt.setTutorMark(
                mark,
                checkResults.stream()
                        .map(checkResult -> new AttemptCheckResult(lastAttempt, checkResult, featureRepository.findAllById(checkResult.getFeatures().stream().map(FeatureDto::getId).toList())))
                        .toList(),
                comment
        );
    }
}
