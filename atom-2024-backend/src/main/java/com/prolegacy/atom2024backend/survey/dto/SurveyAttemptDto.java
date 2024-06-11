package com.prolegacy.atom2024backend.survey.dto;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyAttemptId;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAttemptDto {
    private SurveyAttemptId id;

    private SurveyId surveyId;
    private Instant beginDate;
    private Instant finishDate;
    private UserDto user;
    private Boolean lastAttempt;
    private Long correctAnswerCount;


    private List<SurveyAttemptAnswerDto> answers;
}
