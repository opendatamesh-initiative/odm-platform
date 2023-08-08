package org.opendatamesh.platform.up.notification.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.up.notification.api.resources.ErrorResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MetaServiceClient extends ODMClient {

    public MetaServiceClient(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
    }

    public ResponseEntity createNotification(
            NotificationResource notificationResource
    ) throws JsonProcessingException {

        ResponseEntity postPolicyResponse = rest.postForEntity(
                apiUrl(Routes.METASERVICE_NOTIFICATION),
                notificationResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(postPolicyResponse,
                HttpStatus.CREATED,
                NotificationResource.class);
        return response;

    }

    protected ResponseEntity mapResponseEntity(
            ResponseEntity response,
            HttpStatus acceptedStatusCode,
            Class acceptedClass
    ) throws JsonProcessingException {
        return mapResponseEntity(response, List.of(acceptedStatusCode), acceptedClass, ErrorResource.class);
    }

}
