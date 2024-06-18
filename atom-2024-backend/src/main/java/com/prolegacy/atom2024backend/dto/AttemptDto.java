package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.entities.ids.AttemptId;
import com.prolegacy.atom2024backend.enums.AttemptStatus;
import com.prolegacy.atom2024backend.enums.Mark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AttemptDto {
    private AttemptId id;

    private TopicDto topic;
    private LessonDto lesson;
    private TaskDto task;

    private UserDto user;

    private Instant startDate;
    private Instant endDate;

    private Mark autoMark;
    private List<AttemptCheckResultDto> autoCheckResults;

    private Mark tutorMark;
    private List<AttemptCheckResultDto> tutorCheckResults;
    private String tutorComment;

    private AttemptStatus status;

    private Boolean isNewTryAllowed;
    private Boolean isLastAttempt;

    private List<AttemptFileDto> files;
}
