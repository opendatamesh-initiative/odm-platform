package org.opendatamesh.platform.pp.event.notifier.server.services.proxies.clients;

import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;

public interface EventNotifierNotificationClientFactory {

    NotificationClient getNotificationClient(String serverAddress);

}
