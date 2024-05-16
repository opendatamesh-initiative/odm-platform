package org.opendatamesh.platform.up.observer.api.clients;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.http.ResponseEntity;

public class ConsumeClientImpl extends ODMClient implements ConsumeClient {

    private final RestUtils restUtils;

    public ConsumeClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    public EventNotificationResource consumeEventNotification(EventNotificationResource notificationRes) {
        return restUtils.create(
                apiUrl(ObserverAPIRoutes.CONSUME),
                notificationRes,
                EventNotificationResource.class
        );
    }

    public ResponseEntity<ObjectNode> consumeEventNotificationResponseEntity(
            EventNotificationResource eventNotificationResource
    ) {
        return rest.postForEntity(
                apiUrl(ObserverAPIRoutes.CONSUME),
                eventNotificationResource,
                ObjectNode.class
        );
    }

}
