package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.AttemptFileDto;
import com.prolegacy.atom2024backend.entities.ids.AttemptId;
import com.prolegacy.atom2024backend.enums.AttemptStatus;
import com.prolegacy.atom2024backend.enums.Mark;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Attempt {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private AttemptId id;

    @ManyToOne
    private Topic topic;
    @ManyToOne
    private Lesson lesson;
    @ManyToOne
    private Task task;
    @ManyToOne
    private User user;

    private Instant startDate;
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    private Mark autoMark;
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<AttemptCheckResult> autoCheckResults;
    private Boolean autoCheckFailed = false;

    @Enumerated(EnumType.STRING)
    private Mark tutorMark;
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptCheckResult> tutorCheckResults;
    @Column(columnDefinition = "text")
    private String tutorComment;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    private Boolean isNewTryAllowed = false;

    private Boolean isLastAttempt = true;

    @OneToMany(mappedBy = "attempt", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AttemptFile> files = new ArrayList<>();

    public Attempt(
            Topic topic,
            Lesson lesson,
            Task task,
            User user,
            Instant startDate,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
            Optional<Attempt> lastAttempt
    ) {
        this.topic = topic;
        this.lesson = lesson;
        this.task = task;
        this.user = user;
        this.startDate = startDate;

        lastAttempt.ifPresent(Attempt::close);
    }

    public void finish(Instant endDate, List<AttemptFileDto> files) {
        this.endDate = endDate;
        if (files.isEmpty()) {
            throw new BusinessLogicException("Для отправки задания на проверку должен быть прикреплён хотя бы один файл");
        }
        this.files.addAll(
                files.stream()
                .map(dto -> new AttemptFile(this, dto))
                .toList()
        );
        this.status = AttemptStatus.VALIDATION;
    }

    public void setAutoMart(Mark mark, List<AttemptCheckResult> checkResults) {
        autoMark = mark;
        autoCheckResults = checkResults;
    }

    public void setAutoCheckFailed() {
        autoCheckFailed = true;
    }

    public void failByTime() {
        status = AttemptStatus.DONE;
        tutorMark = Mark.FAILED;
        isNewTryAllowed = true;
    }

    public void setTutorMark(Mark mark, List<AttemptCheckResult> checkResults, String comment) {
        tutorMark = mark;
        tutorCheckResults = checkResults;
        tutorComment = comment;
        isNewTryAllowed = mark.equals(Mark.FAILED);
        status = AttemptStatus.DONE;
    }

    private void close() {
        isLastAttempt = false;
        isNewTryAllowed = false;
    }
}