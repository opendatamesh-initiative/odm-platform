package org.opendatamesh.platform.pp.policy.server.adapter;

import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyV2AdapterService {

    @Autowired
    private List<PolicyV2AdapterNotificationHandler> notificationHandlers;

    @Async
    public void processNotification(NotificationV2Res notification) {
        notificationHandlers.stream()
                .filter(handler -> handler.supports(notification))
                .findFirst()
                .ifPresent(handler -> handler.handle(notification));
    }
}
