package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.NotificationDto;
import com.prolegacy.atom2024backend.entities.QNotification;
import com.prolegacy.atom2024backend.entities.enums.NotificationType;
import com.prolegacy.atom2024backend.entities.ids.NotificationId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class NotificationReader {
    private static final QNotification notification = QNotification.notification;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<NotificationDto> getNewNotifications(NotificationId lastReadId, UserId userId, boolean isTutor) {
        JPAQuery<NotificationDto> query = baseQuery().where(notification.id.gt(lastReadId));
        if (isTutor) query.where(notification.type.eq(NotificationType.VALIDATION));
        else query
                .where(notification.type.in(NotificationType.DONE, NotificationType.TIMEOUT))
                .where(notification.attempt.user.id.eq(userId));
        return query.orderBy(notification.id.desc()).fetch();
    }

    private JPAQuery<NotificationDto> baseQuery() {
        return queryFactory.from(notification)
                .selectDto(
                        NotificationDto.class,
                        UserReader.getFullName(notification.attempt.user).as("attempt$user$fullName"),
                        UserReader.getShortName(notification.attempt.user).as("attempt$user$shortName")
                );
    }
}
