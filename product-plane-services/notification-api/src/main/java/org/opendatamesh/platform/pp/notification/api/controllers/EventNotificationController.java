package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.v1.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.v1.EventNotificationSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventNotificationController {

    EventNotificationResource updateEventNotification(Long notificationId, EventNotificationResource eventNotificationResource);

    EventNotificationResource readOneEventNotification(Long notificationId);

    Page<EventNotificationResource> searchEventNotifications(Pageable pageable, EventNotificationSearchOptions searchOption);

}
