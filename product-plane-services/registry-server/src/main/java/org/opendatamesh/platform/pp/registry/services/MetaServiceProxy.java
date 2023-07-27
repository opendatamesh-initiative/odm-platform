package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.platform.pp.registry.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.up.notification.api.clients.MetaServiceClient;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MetaServiceProxy extends MetaServiceClient {

    @Value("${skipmetaservice}")
    private String skipmetaservice;

    private static final Logger logger = LoggerFactory.getLogger(MetaServiceProxy.class);

    public MetaServiceProxy(@Value("${metaserviceaddress}") final String serverAddress) {
        super(serverAddress);
    }

    public void postEventToMetaService(EventResource event) {

        NotificationResource notification = new NotificationResource();
        notification.setEvent(event);

        try {

            ResponseEntity<NotificationResource> responseEntity = createNotification(notification);

            if(responseEntity.getStatusCode().is2xxSuccessful()){
                notification = responseEntity.getBody();
                logger.debug("Successfuly loaded information to Meta service system: " + notification.toString());
            } else {
                throw new BadGatewayException(
                        OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                        "An error occurred while comunicating with the metaService");
            }

        } catch (Exception e) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "metaService not reachable"
            );
        }
    }
}
