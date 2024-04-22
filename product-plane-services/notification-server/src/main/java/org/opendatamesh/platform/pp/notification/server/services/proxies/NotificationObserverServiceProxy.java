package org.opendatamesh.platform.pp.notification.server.services.proxies;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.server.services.proxies.clients.NotificationObserverClientFactory;
import org.opendatamesh.platform.up.observer.api.clients.ConsumeClient;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationObserverServiceProxy {

    @Autowired
    NotificationObserverClientFactory notificationObserverClientFactory;

    private static final Logger logger = LoggerFactory.getLogger(NotificationObserverServiceProxy.class);

    public void postEventToNotificationService(EventResource event, String serverAddress) {

        ConsumeClient observerClient = notificationObserverClientFactory.getNotificationClient(serverAddress);

        EventNotificationResource notification = new EventNotificationResource();
        notification.setEvent(event);

        try {
            observerClient.consumeEventNotification(notification);
            logger.debug("Successfully dispatched Event Notification to Observer: " + notification);
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    e.getMessage(),
                    e
            );
        }

    }

}
