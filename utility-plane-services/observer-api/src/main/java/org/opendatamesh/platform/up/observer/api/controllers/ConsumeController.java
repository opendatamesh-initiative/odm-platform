package org.opendatamesh.platform.up.observer.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.v1.EventNotificationResource;

public interface ConsumeController {

    EventNotificationResource consumeEventNotification(EventNotificationResource notificationRes);

}
