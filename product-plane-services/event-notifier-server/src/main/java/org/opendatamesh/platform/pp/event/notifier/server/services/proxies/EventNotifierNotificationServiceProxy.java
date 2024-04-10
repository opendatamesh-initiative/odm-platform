package org.opendatamesh.platform.pp.event.notifier.server.services.proxies;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.event.notifier.server.services.proxies.clients.EventNotifierNotificationClientGetter;
import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventNotifierNotificationServiceProxy {

    @Autowired
    EventNotifierNotificationClientGetter eventNotifierNotificationClientGetter;

    private static final Logger logger = LoggerFactory.getLogger(EventNotifierNotificationServiceProxy.class);

    public void postEventToNotificationService(EventResource event, String serverAddress) {

        NotificationClient notificationClient = eventNotifierNotificationClientGetter.getNotificationClient(serverAddress);

        NotificationResource notification = new NotificationResource();
        notification.setEvent(event);

        try {
            NotificationResource notificationResource = notificationClient.createNotification(notification);
            logger.debug("Successfully loaded information to Meta service system: " + notificationResource.toString());
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    e.getMessage(),
                    e
            );
        }

    }

}
