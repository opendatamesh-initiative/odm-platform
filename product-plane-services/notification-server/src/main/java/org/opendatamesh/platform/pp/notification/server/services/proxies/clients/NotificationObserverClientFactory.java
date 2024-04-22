package org.opendatamesh.platform.pp.notification.server.services.proxies.clients;

import org.opendatamesh.platform.up.observer.api.clients.ConsumeClient;

public interface NotificationObserverClientFactory {

    ConsumeClient getNotificationClient(String serverAddress);

}
