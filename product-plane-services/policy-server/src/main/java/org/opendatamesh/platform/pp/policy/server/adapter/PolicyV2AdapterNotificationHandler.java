package org.opendatamesh.platform.pp.policy.server.adapter;

import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;

public interface PolicyV2AdapterNotificationHandler {
    boolean supports(NotificationV2Res notification);

    void handle(NotificationV2Res notification);
}
