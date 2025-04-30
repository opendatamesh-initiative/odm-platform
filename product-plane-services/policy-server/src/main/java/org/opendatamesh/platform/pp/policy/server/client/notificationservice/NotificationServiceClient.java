package org.opendatamesh.platform.pp.policy.server.client.notificationservice;

import org.opendatamesh.platform.pp.policy.server.resources.notificationservice.NotificationEventResource;

public interface NotificationServiceClient {
    void notifyEvent(NotificationEventResource eventResource);
}
