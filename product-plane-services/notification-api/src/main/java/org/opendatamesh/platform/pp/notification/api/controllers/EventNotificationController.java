package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventNotificationController {

    //EventNotificationResource createNotification(EventNotificationResource eventNotificationResource);

    EventNotificationResource updateEventNotification(Long notificationId, EventNotificationResource eventNotificationResource);

    EventNotificationResource readOneEventNotification(Long notificationId);

    Page<EventNotificationResource> searchEventNotifications(Pageable pageable, EventNotificationSearchOptions searchOption);

    //void deleteNotification(Long notificationId);

}
