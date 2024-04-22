package org.opendatamesh.platform.up.observer.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ConsumeClientImpl extends ODMClient implements ConsumeClient {

    private final RestUtils restUtils;

    public ConsumeClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    @Override
    public void consumeEventNotification(EventNotificationResource notificationRes) {
        restUtils.create(
                apiUrl(ObserverAPIRoutes.CONSUME),
                notificationRes,
                EventNotificationResource.class
        );
    }

    public ResponseEntity consumeEventNotificationResponseEntity(
            EventNotificationResource eventNotificationResource
    ) throws JsonProcessingException {

        ResponseEntity postNotificationResponse = rest.postForEntity(
                apiUrl(ObserverAPIRoutes.CONSUME),
                eventNotificationResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(postNotificationResponse,
                HttpStatus.CREATED,
                Void.class
        );

        return response;

    }

    protected ResponseEntity mapResponseEntity(
            ResponseEntity response,
            HttpStatus acceptedStatusCode,
            Class acceptedClass
    ) throws JsonProcessingException {
        return mapResponseEntity(response, List.of(acceptedStatusCode), acceptedClass, ErrorRes.class);
    }

}
