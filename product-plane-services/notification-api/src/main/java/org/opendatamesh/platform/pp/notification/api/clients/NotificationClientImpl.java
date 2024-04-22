package org.opendatamesh.platform.pp.notification.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.resources.*;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class NotificationClientImpl extends ODMClient implements NotificationClient {

    private final RestUtils restUtils;

    public NotificationClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    public NotificationClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }


    // ===============================================================================
    // Observers
    // ===============================================================================

    public ObserverResource addObserver(ObserverResource observerResource) {
        return restUtils.create(apiUrl(NotificationAPIRoutes.OBSERVERS), observerResource, ObserverResource.class);
    }

    public ObserverResource updateObserver(Long id, ObserverResource observerResource) {
        return restUtils.put(apiUrlOfItem(NotificationAPIRoutes.OBSERVERS), id, observerResource, ObserverResource.class);
    }

    public Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions) {
        return restUtils.getPage(apiUrl(NotificationAPIRoutes.OBSERVERS), pageable, searchOptions, ObserverResource.class);
    }

    public ObserverResource getObserver(Long id) {
        return restUtils.get(apiUrlOfItem(NotificationAPIRoutes.OBSERVERS), id, ObserverResource.class);
    }

    public void removeObserver(Long id) {
        restUtils.delete(apiUrlOfItem(NotificationAPIRoutes.OBSERVERS), id);
    }


    // ===============================================================================
    // Notifications
    // ===============================================================================

    @Override
    public EventNotificationResource createNotification(EventNotificationResource eventNotificationResource) {
        return restUtils.create(
                apiUrl(NotificationAPIRoutes.NOTIFICATIONS),
                eventNotificationResource,
                EventNotificationResource.class
        );
    }

    @Override
    public EventNotificationResource updateNotification(Long notificationId, EventNotificationResource eventNotificationResource) {
        return restUtils.put(
                apiUrlOfItem(NotificationAPIRoutes.NOTIFICATIONS),
                notificationId,
                eventNotificationResource,
                EventNotificationResource.class
        );
    }

    @Override
    public EventNotificationResource readOneNotification(Long notificationId) {
        return restUtils.get(
                apiUrlOfItem(NotificationAPIRoutes.NOTIFICATIONS),
                notificationId,
                EventNotificationResource.class
        );
    }

    @Override
    public Page<EventNotificationResource> searchNotifications(Pageable pageable, EventNotificationSearchOptions searchOption) {
        return restUtils.getPage(
                apiUrl(NotificationAPIRoutes.NOTIFICATIONS),
                pageable,
                searchOption,
                EventNotificationResource.class
        );
    }

    @Override
    public void deleteNotification(Long notificationId) {
        restUtils.delete(apiUrlOfItem(NotificationAPIRoutes.NOTIFICATIONS), notificationId);
    }


    // ===============================================================================
    // Dispatch
    // ===============================================================================

    public void notifyEvent(EventResource eventResource) {
        restUtils.genericPost(apiUrl(NotificationAPIRoutes.DISPATCH), eventResource, EventResource.class);
    }


    // ===============================================================================
    // Response Entity version
    // ===============================================================================

    public ResponseEntity<ObjectNode> addObserverResponseEntity(ObserverResource observerResource) {
        return rest.exchange(
                apiUrl(NotificationAPIRoutes.OBSERVERS),
                HttpMethod.POST,
                new HttpEntity<>(observerResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updateObserverResponseEntity(Long id, ObserverResource observerResource) {
        return rest.exchange(
                apiUrlOfItem(NotificationAPIRoutes.OBSERVERS),
                HttpMethod.PUT,
                new HttpEntity<>(observerResource),
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> getObserversResponseEntity() {
        return rest.exchange(
                apiUrl(NotificationAPIRoutes.OBSERVERS),
                HttpMethod.GET,
                null,
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> getObserverResponseEntity(Long id) {
        return rest.exchange(
                apiUrlOfItem(NotificationAPIRoutes.OBSERVERS),
                HttpMethod.GET,
                null,
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> removeObserverResponseEntity(Long id) {
        return rest.exchange(
                apiUrlOfItem(NotificationAPIRoutes.OBSERVERS),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> notifyEventResponseEntity(EventResource eventResource) {
        return rest.exchange(
                apiUrl(NotificationAPIRoutes.DISPATCH),
                HttpMethod.POST,
                new HttpEntity<>(eventResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> createNotificationResponseEntity(EventNotificationResource eventNotificationResource) {
        return rest.exchange(
                apiUrl(NotificationAPIRoutes.NOTIFICATIONS),
                HttpMethod.POST,
                new HttpEntity<>(eventNotificationResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updateNotificationResponseEntity(Long notificationId, EventNotificationResource eventNotificationResource) {
        return rest.exchange(
                apiUrlOfItem(NotificationAPIRoutes.NOTIFICATIONS),
                HttpMethod.PUT,
                new HttpEntity<>(eventNotificationResource),
                ObjectNode.class,
                notificationId
        );
    }

    public ResponseEntity<ObjectNode> readOneNotificationResponseEntity(Long notificationId) {
        return rest.exchange(
                apiUrlOfItem(NotificationAPIRoutes.NOTIFICATIONS),
                HttpMethod.GET,
                null,
                ObjectNode.class,
                notificationId
        );
    }

    public ResponseEntity<ObjectNode> searchNotificationsResponseEntity(Pageable pageable, EventNotificationSearchOptions searchOption) {
        Map<String, Object> queryParams = new HashMap<>();
        if(searchOption.getEventType() != null)
            queryParams.put("eventType", searchOption.getEventType());
        if(searchOption.getNotificationStatus() != null)
            queryParams.put("notificationStatus", searchOption.getNotificationStatus());
        if(queryParams.size() >= 0)
            return rest.exchange(
                    apiUrl(NotificationAPIRoutes.NOTIFICATIONS, queryParams),
                    HttpMethod.GET,
                    null,
                    ObjectNode.class
            );
        else
            return rest.exchange(
                    apiUrl(NotificationAPIRoutes.NOTIFICATIONS),
                    HttpMethod.GET,
                    null,
                    ObjectNode.class
            );
    }

    public ResponseEntity<ObjectNode> deleteNotificationResponseEntity(Long notificationId) {
        return rest.exchange(
                apiUrlOfItem(NotificationAPIRoutes.NOTIFICATIONS),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                notificationId
        );
    }

}
