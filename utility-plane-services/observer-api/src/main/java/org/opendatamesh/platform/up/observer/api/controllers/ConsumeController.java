package org.opendatamesh.platform.up.observer.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;

public interface ConsumeController {

    void consumeEventNotification(EventNotificationResource notificationRes);

}
