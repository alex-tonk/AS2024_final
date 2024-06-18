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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
    private Boolean autoCheckFailed;

    private Mark tutorMark;
    private List<AttemptCheckResultDto> tutorCheckResults;
    private String tutorComment;

    private AttemptStatus status;

    private Boolean isNewTryAllowed;
    private Boolean isLastAttempt;

    private List<AttemptFileDto> files;

    public String getStatusLocale() {
        return Optional.ofNullable(this.status)
                .map(s -> s.locale)
                .orElse(null);
    }

    public String getAutoStatus() {
        if (this.getStatus() == AttemptStatus.VALIDATION) {
            if (this.autoMark != null) {
                return "Проверено";
            } else if (Optional.ofNullable(this.autoCheckFailed).orElse(false)) {
                return "Ошибка";
            }
            return "В обработке";
        } else if (this.getStatus() == AttemptStatus.DONE) {
            if (this.autoMark != null) {
                return "Проверено";
            } else if (Optional.ofNullable(this.autoCheckFailed).orElse(false)) {
                return "Ошибка";
            }
            return "Пропущено";
        }
        return null;
    }

    public String getFormattedStartDate() {
        return Optional.ofNullable(this.startDate)
                .map(d -> LocalDateTime.ofInstant(d, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .orElse(null);
    }

    public String getFormattedEndDate() {
        return Optional.ofNullable(this.endDate)
                .map(d -> LocalDateTime.ofInstant(d, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .orElse(null);
    }

    public String getAutoMarkLocale() {
        return Optional.ofNullable(this.autoMark)
                .map(m -> m.description)
                .orElse(null);
    }

    public String getTutorMarkLocale() {
        return Optional.ofNullable(this.tutorMark)
                .map(m -> m.description)
                .orElse(null);
    }

    public Boolean getArchived() {
        return Optional.ofNullable(this.isLastAttempt)
                .map(m -> !this.isLastAttempt)
                .orElse(null);
    }
}
