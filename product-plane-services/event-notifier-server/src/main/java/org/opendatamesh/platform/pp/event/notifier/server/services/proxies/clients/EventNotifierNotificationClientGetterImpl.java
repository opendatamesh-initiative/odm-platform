package org.opendatamesh.platform.pp.event.notifier.server.services.proxies.clients;

import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.up.notification.api.clients.NotificationClientImpl;
import org.springframework.stereotype.Service;

@Service
public class EventNotifierNotificationClientGetterImpl implements EventNotifierNotificationClientGetter {
    @Override
    public NotificationClient getNotificationClient(String serverAddress) {
        return new NotificationClientImpl(serverAddress);
    }

}
