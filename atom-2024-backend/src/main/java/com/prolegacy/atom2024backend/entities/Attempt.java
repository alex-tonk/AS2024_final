package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.auth.entities.User;
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

    @OneToMany
    private List<File> files = new ArrayList<>();

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

    public void finish(Instant endDate, List<File> files) {
        this.endDate = endDate;
        this.files = files;
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
        isNewTryAllowed = true;
    }

    public void setTutorMark(Mark mark, List<AttemptCheckResult> checkResults, String comment) {
        tutorMark = mark;
        tutorCheckResults = checkResults;
        tutorComment = comment;
        isNewTryAllowed = false;
        status = AttemptStatus.DONE;
    }

    private void close() {
        isLastAttempt = false;
        isNewTryAllowed = false;
    }
}
