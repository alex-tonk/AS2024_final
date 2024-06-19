package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Notification;
import com.prolegacy.atom2024backend.entities.ids.NotificationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, NotificationId> {
}
