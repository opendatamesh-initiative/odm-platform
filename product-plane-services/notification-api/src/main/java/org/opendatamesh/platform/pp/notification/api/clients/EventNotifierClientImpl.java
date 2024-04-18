package org.opendatamesh.platform.pp.notification.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.NotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class EventNotifierClientImpl extends ODMClient implements EventNotifierClient {

    private final RestUtils restUtils;

    public EventNotifierClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    public EventNotifierClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }


    // ===============================================================================
    // Observers
    // ===============================================================================

    public ObserverResource addObserver(ObserverResource observerResource) {
        return restUtils.create(apiUrl(EventNotifierAPIRoutes.OBSERVERS), observerResource, ObserverResource.class);
    }

    public ObserverResource updateObserver(Long id, ObserverResource observerResource) {
        return restUtils.put(apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS), id, observerResource, ObserverResource.class);
    }

    public Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions) {
        return restUtils.getPage(apiUrl(EventNotifierAPIRoutes.OBSERVERS), pageable, searchOptions, ObserverResource.class);
    }

    public ObserverResource getObserver(Long id) {
        return restUtils.get(apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS), id, ObserverResource.class);
    }

    public void removeObserver(Long id) {
        restUtils.delete(apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS), id);
    }


    // ===============================================================================
    // Notifications
    // ===============================================================================

    @Override
    public NotificationResource createNotification(NotificationResource notificationResource) {
        return restUtils.create(
                apiUrl(EventNotifierAPIRoutes.NOTIFICATIONS),
                notificationResource,
                NotificationResource.class
        );
    }

    @Override
    public NotificationResource updateNotification(Long notificationId, NotificationResource notificationResource) {
        return restUtils.put(
                apiUrlOfItem(EventNotifierAPIRoutes.NOTIFICATIONS),
                notificationId,
                notificationResource,
                NotificationResource.class
        );
    }

    @Override
    public NotificationResource readOneNotification(Long notificationId) {
        return restUtils.get(
                apiUrlOfItem(EventNotifierAPIRoutes.NOTIFICATIONS),
                notificationId,
                NotificationResource.class
        );
    }

    @Override
    public Page<NotificationResource> searchNotifications(Pageable pageable, NotificationSearchOptions searchOption) {
        return restUtils.getPage(
                apiUrl(EventNotifierAPIRoutes.NOTIFICATIONS),
                pageable,
                searchOption,
                NotificationResource.class
        );
    }

    @Override
    public void deleteNotification(Long notificationId) {
        restUtils.delete(apiUrlOfItem(EventNotifierAPIRoutes.NOTIFICATIONS), notificationId);
    }


    // ===============================================================================
    // Dispatch
    // ===============================================================================

    public void notifyEvent(EventResource eventResource) {
        restUtils.genericPost(apiUrl(EventNotifierAPIRoutes.DISPATCH), eventResource, EventResource.class);
    }


    // ===============================================================================
    // Response Entity version
    // ===============================================================================

    public ResponseEntity<ObjectNode> addObserverResponseEntity(ObserverResource observerResource) {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.POST,
                new HttpEntity<>(observerResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updateObserverResponseEntity(Long id, ObserverResource observerResource) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.PUT,
                new HttpEntity<>(observerResource),
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> getObserversResponseEntity() {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.GET,
                null,
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> getObserverResponseEntity(Long id) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.GET,
                null,
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> removeObserverResponseEntity(Long id) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> notifyEventResponseEntity(EventResource eventResource) {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.DISPATCH),
                HttpMethod.POST,
                new HttpEntity<>(eventResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> createNotificationResponseEntity(NotificationResource notificationResource) {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.NOTIFICATIONS),
                HttpMethod.POST,
                new HttpEntity<>(notificationResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updateNotificationResponseEntity(Long notificationId, NotificationResource notificationResource) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.NOTIFICATIONS),
                HttpMethod.PUT,
                new HttpEntity<>(notificationResource),
                ObjectNode.class,
                notificationId
        );
    }

    public ResponseEntity<ObjectNode> readOneNotificationResponseEntity(Long notificationId) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.NOTIFICATIONS),
                HttpMethod.GET,
                null,
                ObjectNode.class,
                notificationId
        );
    }

    public ResponseEntity<ObjectNode> searchNotificationsResponseEntity(Pageable pageable, NotificationSearchOptions searchOption) {
        Map<String, Object> queryParams = new HashMap<>();
        if(searchOption.getEventType() != null)
            queryParams.put("eventType", searchOption.getEventType());
        if(searchOption.getNotificationStatus() != null)
            queryParams.put("notificationStatus", searchOption.getNotificationStatus());
        if(queryParams.size() >= 0)
            return rest.exchange(
                    apiUrl(EventNotifierAPIRoutes.NOTIFICATIONS, queryParams),
                    HttpMethod.GET,
                    null,
                    ObjectNode.class
            );
        else
            return rest.exchange(
                    apiUrl(EventNotifierAPIRoutes.NOTIFICATIONS),
                    HttpMethod.GET,
                    null,
                    ObjectNode.class
            );
    }

    public ResponseEntity<ObjectNode> deleteNotificationResponseEntity(Long notificationId) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.NOTIFICATIONS),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                notificationId
        );
    }

}
