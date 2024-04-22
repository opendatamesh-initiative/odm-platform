package org.opendatamesh.platform.pp.notification.server.services.proxies.clients;

import org.opendatamesh.platform.up.observer.api.clients.ConsumeClient;
import org.opendatamesh.platform.up.observer.api.clients.ConsumeClientImpl;
import org.springframework.stereotype.Service;

@Service
public class NotificationObserverClientFactoryImpl implements NotificationObserverClientFactory {
    @Override
    public ConsumeClient getNotificationClient(String serverAddress) {
        return new ConsumeClientImpl(serverAddress);
    }

}
