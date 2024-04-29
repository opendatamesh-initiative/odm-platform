package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventNotificationIT extends ODMNotificationIT {

    // ======================================================================================
    // UPDATE EventNotification
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateEventNotification() {

        // Resources + Creation
        // Observer
        ObserverResource observerResource = createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        // Event
        EventResource eventToDispatch = createEventResource(ODMNotificationResources.RESOURCE_EVENT);
        // Dispatch event to force EventNotification creation
        notifyEvent(eventToDispatch);

        // Find and update resource
        ResponseEntity<ObjectNode> getResponse = notificationClient.searchEventNotificationsResponseEntity(
                new EventNotificationSearchOptions()
        );
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<EventNotificationResource> notifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(notifications.size()).isEqualTo(1);
        EventNotificationResource notification = notifications.get(0);
        notification.setReceivedAt(new Date(System.currentTimeMillis()));
        notification.setProcessingOutput("OK");
        notification.setStatus(EventNotificationStatus.PROCESSED);
        notification.setProcessedAt(new Date(System.currentTimeMillis()));

        // PUT request
        ResponseEntity<ObjectNode> putResponse = notificationClient.updateEventNotificationResponseEntity(
                notification.getId(), notification
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        notification = mapper.convertValue(putResponse.getBody(), EventNotificationResource.class);

        assertThat(notification.getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch);
        assertThat(notification.getObserver()).usingRecursiveComparison().isEqualTo(observerResource);
        assertThat(notification.getStatus()).isEqualTo(EventNotificationStatus.PROCESSED);
        assertThat(notification.getProcessingOutput()).isEqualTo("OK");
        assertThat(notification.getProcessedAt()).isNotNull();
        assertThat(notification.getReceivedAt()).isNotNull();
        assertThat(notification.getReceivedAt()).isBeforeOrEqualTo(notification.getProcessedAt());

    }


    // ======================================================================================
    // READ ALL EventNotifications
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllEventNotifications() {

        // 1 Observer, 2 Events

        // Resources + Creation
        ObserverResource observerResource = createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        EventResource eventToDispatch = createEventResource(ODMNotificationResources.RESOURCE_EVENT);
        EventResource eventToDispatch2 = createEventResource(ODMNotificationResources.RESOURCE_EVENT_2);
        notifyEvent(eventToDispatch);
        notifyEvent(eventToDispatch2);

        // GET request
        EventNotificationSearchOptions searchOptions = new EventNotificationSearchOptions();
        ResponseEntity<ObjectNode> getResponse = notificationClient.searchEventNotificationsResponseEntity(searchOptions);
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<EventNotificationResource> notifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(notifications.size()).isEqualTo(2);
        observerResource.setId(1L);
        eventToDispatch.setId(1L);
        assertThat(notifications.get(0).getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch);
        assertThat(notifications.get(0).getObserver()).usingRecursiveComparison().isEqualTo(observerResource);
        eventToDispatch2.setId(2L);
        assertThat(notifications.get(1).getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch2);
        assertThat(notifications.get(1).getObserver()).usingRecursiveComparison().isEqualTo(observerResource);

        // Filtered GET requests - by event
        searchOptions.setEventType(EventType.DATA_PRODUCT_CREATED);
        getResponse = notificationClient.searchEventNotificationsResponseEntity(searchOptions);
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        notifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(notifications.size()).isEqualTo(1);
        assertThat(notifications.get(0).getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch);
        assertThat(notifications.get(0).getObserver()).usingRecursiveComparison().isEqualTo(observerResource);

        // Filtered GET requests - by status
        searchOptions.setEventType(null);
        searchOptions.setNotificationStatus(EventNotificationStatus.PROCESS_ERROR);
        getResponse = notificationClient.searchEventNotificationsResponseEntity(searchOptions);
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        notifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(notifications.size()).isEqualTo(0);

    }


    // ======================================================================================
    // READ ONE EventNotification
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneEventNotification() throws JsonProcessingException {

        // Resources + Creation
        ObserverResource observerResource = createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        EventResource eventToDispatch = createEventResource(ODMNotificationResources.RESOURCE_EVENT);
        notifyEvent(eventToDispatch);

        // GET request
        ResponseEntity<ObjectNode> getResponse = notificationClient.readOneEventNotificationResponseEntity(1L);
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        EventNotificationResource notification = mapper.convertValue(getResponse.getBody(), EventNotificationResource.class);

        observerResource.setId(1L);
        assertThat(notification.getObserver()).usingRecursiveComparison().isEqualTo(observerResource);
        eventToDispatch.setId(1L);
        assertThat(notification.getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch);
        assertThat(notification.getStatus()).isNotNull();

    }
    
}
