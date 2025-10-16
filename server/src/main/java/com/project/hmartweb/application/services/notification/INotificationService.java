package com.project.hmartweb.application.services.notification;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.domain.dtos.NotificationDTO;
import com.project.hmartweb.domain.entities.Notification;
import com.project.hmartweb.domain.paginate.PaginationDTO;

import java.util.List;
import java.util.UUID;

public interface INotificationService extends IBaseService<Notification, NotificationDTO, UUID> {
    Notification readNotification(UUID id);

    PaginationDTO<Notification> getAllByUser(UUID userId, Integer page, Integer perPage);

    List<Notification> getAllByUserAndRead(UUID userId);

    void readAllNotifications(UUID userId);
}
