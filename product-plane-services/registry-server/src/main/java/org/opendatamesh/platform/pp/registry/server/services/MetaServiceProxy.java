package org.opendatamesh.platform.pp.registry.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.up.notification.api.resources.ErrorResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MetaServiceProxy extends NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(MetaServiceProxy.class);

    public MetaServiceProxy(@Value("${odm.utilityPlane.notificationServices.blindata.address}") final String serverAddress) {
        super(serverAddress);
    }

    public void postEventToMetaService(EventResource event) {

        NotificationResource notification = new NotificationResource();
        notification.setEvent(event);

        try {

            ResponseEntity responseEntity = createNotification(notification);

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
