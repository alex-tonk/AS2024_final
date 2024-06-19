package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.dto.NotificationDto;
import com.prolegacy.atom2024backend.entities.ids.NotificationId;
import com.prolegacy.atom2024backend.enums.Role;
import com.prolegacy.atom2024backend.readers.NotificationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("notifications")
@TypescriptEndpoint
public class NotificationController {
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private NotificationReader notificationReader;

    @GetMapping
    public List<NotificationDto> getNewNotifications(@RequestParam NotificationId lastReadNotificationId) {
        User user = userProvider.get();
        boolean isTutor = user.getRoles().stream().anyMatch(role -> role.getName().equals(Role.TUTOR.getRoleName()));
        return notificationReader.getNewNotifications(lastReadNotificationId, user.getId(), isTutor);
    }
}
