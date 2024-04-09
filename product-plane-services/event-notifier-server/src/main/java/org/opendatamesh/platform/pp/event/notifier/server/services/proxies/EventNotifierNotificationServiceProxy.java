package org.opendatamesh.platform.pp.event.notifier.server.services.proxies;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.up.notification.api.resources.ErrorResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public final class EventNotifierNotificationServiceProxy {

    private static final Logger logger = LoggerFactory.getLogger(EventNotifierNotificationServiceProxy.class);

    public static void postEventToNotificationService(EventResource event, String serverAddress) {

        NotificationClient notificationClient = new NotificationClient(serverAddress);

        NotificationResource notification = new NotificationResource();
        notification.setEvent(event);

        try {

            ResponseEntity responseEntity = notificationClient.createNotification(notification);

            if(responseEntity.getStatusCode().is2xxSuccessful()){
                notification = (NotificationResource) responseEntity.getBody();
                logger.debug("Successfully loaded information to Meta service system: " + notification.toString());
            } else {
                ErrorResource error = (ErrorResource) responseEntity.getBody();
                throw new BadGatewayException(
                        ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                        error.getMessage()
                );
            }

        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    e.getMessage()
            );
        }
    }

}
