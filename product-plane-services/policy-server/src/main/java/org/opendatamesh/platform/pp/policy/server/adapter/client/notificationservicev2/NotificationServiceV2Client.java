package org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2;

import java.util.List;

public interface NotificationServiceV2Client {
    void assertConnection();
    void subscribeToEvents(List<String> eventTypes);
    void notifyEvent(Object event);
    void processingSuccess(Long notificationId);
    void processingFailure(Long notificationId);
}