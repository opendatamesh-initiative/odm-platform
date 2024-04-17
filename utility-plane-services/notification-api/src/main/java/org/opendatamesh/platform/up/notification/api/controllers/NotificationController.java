package org.opendatamesh.platform.up.notification.api.controllers;

import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;

import java.util.List;

public interface NotificationController {

    NotificationResource createNotification(NotificationResource notificationRes);

    NotificationResource readOneNotification(Long notificationId);

    List<NotificationResource> searchNotifications(String eventType, String notificationStatus);

    void deleteNotification(Long notificationId);

}
