package org.opendatamesh.platform.up.notification.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.notification.api.resources.ErrorResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class NotificationClientImpl extends ODMClient implements NotificationClient {

    private final RestUtils restUtils;

    public NotificationClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    @Override
    public NotificationResource createNotification(NotificationResource notificationRes) {
        return restUtils.create(
                apiUrl(NotificationAPIRoutes.METASERVICE_NOTIFICATION),
                notificationRes,
                NotificationResource.class
        );
    }

    @Override
    public NotificationResource readOneNotification(Long notificationId) {
        return restUtils.get(
                apiUrlOfItem(NotificationAPIRoutes.METASERVICE_NOTIFICATION),
                notificationId,
                NotificationResource.class
        );
    }

    @Override
    public List<NotificationResource> searchNotifications(String eventType, String notificationStatus) {
        List<String> params = new ArrayList<>();
        if(eventType != null) params.add("eventType=" + eventType);
        if(notificationStatus != null) params.add("notificationStatus=" + notificationStatus);
        NotificationResource[] responseArray = restUtils.get(
                apiUrlOfItem(NotificationAPIRoutes.METASERVICE_NOTIFICATION),
                NotificationResource[].class,
                null
        );
        return List.of(responseArray);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        restUtils.delete(apiUrlOfItem(NotificationAPIRoutes.METASERVICE_NOTIFICATION), notificationId);
    }

    public ResponseEntity createNotificationResponseEntity(
            NotificationResource notificationResource
    ) throws JsonProcessingException {

        ResponseEntity postNotificationResponse = rest.postForEntity(
                apiUrl(NotificationAPIRoutes.METASERVICE_NOTIFICATION),
                notificationResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(postNotificationResponse,
                HttpStatus.CREATED,
                NotificationResource.class);
        return response;

    }

    public ResponseEntity readOneNotificationResponseEntity(
            Long notificationId
    ) throws JsonProcessingException {
        ResponseEntity getNotificationResponse = rest.getForEntity(
                apiUrlOfItem(NotificationAPIRoutes.METASERVICE_NOTIFICATION),
                Object.class,
                notificationId
        );

        ResponseEntity response = mapResponseEntity(getNotificationResponse,
                HttpStatus.OK,
                NotificationResource.class);
        return response;

    }


    public ResponseEntity searchNotificationsResponseEntity(
            String eventType,
            String notificationStatus
    )throws JsonProcessingException {
        List<String> params = new ArrayList<>();
        if(eventType != null) params.add("eventType=" + eventType);
        if(notificationStatus != null) params.add("notificationStatus=" + notificationStatus);

        ResponseEntity getNotificationsResponse = rest.getForEntity(
                apiUrlWithQueryParams(NotificationAPIRoutes.METASERVICE_NOTIFICATION, params),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(getNotificationsResponse,
                HttpStatus.OK,
                NotificationResource[].class);
        return response;
    }

    public ResponseEntity deleteNotificationResponseEntity(
            Long notificationId
    ) throws JsonProcessingException {

        ResponseEntity deleteNotificationsResponse = rest.exchange(apiUrlOfItem(NotificationAPIRoutes.METASERVICE_NOTIFICATION),
                HttpMethod.DELETE,
                null,
                Object.class,
                notificationId);

        ResponseEntity response = mapResponseEntity(deleteNotificationsResponse,
                HttpStatus.OK,
                Void.class//,
                //ErrorResource[].class
        );
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
