package org.opendatamesh.platform.pp.event.notifier.server.services.proxies.clients;

import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;

public interface EventNotifierNotificationClientGetter {

    NotificationClient getNotificationClient(String serverAddress);

}
