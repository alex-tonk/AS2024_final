package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.enums.NotificationType;
import com.prolegacy.atom2024backend.entities.ids.NotificationId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private NotificationId id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private Attempt attempt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification(Attempt attempt, NotificationType type) {
        this.attempt = attempt;
        this.type = type;
    }
}
