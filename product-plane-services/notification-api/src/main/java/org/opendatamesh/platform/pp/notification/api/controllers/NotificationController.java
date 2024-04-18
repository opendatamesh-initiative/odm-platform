package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.NotificationSearchOptions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationController {

    NotificationResource createNotification(NotificationResource notificationResource);

    NotificationResource updateNotification(Long notificationId, NotificationResource notificationResource);

    NotificationResource readOneNotification(Long notificationId);

    Page<NotificationResource> searchNotifications(Pageable pageable, NotificationSearchOptions searchOption);

    void deleteNotification(Long notificationId);

}
