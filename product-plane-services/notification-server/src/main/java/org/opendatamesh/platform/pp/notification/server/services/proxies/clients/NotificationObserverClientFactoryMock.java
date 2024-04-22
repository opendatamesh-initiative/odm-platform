package org.opendatamesh.platform.pp.notification.server.services.proxies.clients;

import org.opendatamesh.platform.up.observer.api.clients.ConsumeClient;
import org.opendatamesh.platform.up.observer.api.clients.ConsumeClientMock;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"test", "testpostgresql", "testmysql"})
@Primary
public class NotificationObserverClientFactoryMock implements NotificationObserverClientFactory {

    @Override
    public ConsumeClient getNotificationClient(String serverAddress) {
        return new ConsumeClientMock();
    }

}
