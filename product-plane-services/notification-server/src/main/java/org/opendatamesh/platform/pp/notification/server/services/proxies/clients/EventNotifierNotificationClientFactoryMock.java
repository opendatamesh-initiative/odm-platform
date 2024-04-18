package org.opendatamesh.platform.pp.notification.server.services.proxies.clients;

import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.up.notification.api.clients.NotificationClientMock;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"test", "testpostgresql", "testmysql"})
@Primary
public class EventNotifierNotificationClientFactoryMock implements EventNotifierNotificationClientFactory {

    @Override
    public NotificationClient getNotificationClient(String serverAddress) {
        return new NotificationClientMock();
    }

}
