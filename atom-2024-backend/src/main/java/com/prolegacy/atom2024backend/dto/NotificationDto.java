package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.enums.NotificationType;
import com.prolegacy.atom2024backend.entities.ids.NotificationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NotificationDto {
    private NotificationId id;
    private AttemptDto attempt;
    private NotificationType type;
}
