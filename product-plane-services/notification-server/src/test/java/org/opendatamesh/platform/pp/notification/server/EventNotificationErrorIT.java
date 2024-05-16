package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventNotificationErrorIT extends ODMNotificationIT {

    // ======================================================================================
    // UPDATE EventNotification
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateEventNotification_Error400xx() {

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

        // 40003 - Notification is empty - Notification object cannot be null
        ResponseEntity<ObjectNode> putResponse = notificationClient.updateEventNotificationResponseEntity(
                notification.getId(), null
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.BAD_REQUEST,
                NotificationApiStandardErrors.SC400_03_NOTIFICATION_IS_EMTPY,
                "Notification object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateEventNotification_Error404xx() {

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

        // 40403 - Notification not found - Resource with ID [id] not found

        // PUT request
        ResponseEntity<ObjectNode> putResponse = notificationClient.updateEventNotificationResponseEntity(
                17L, notification
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.NOT_FOUND,
                NotificationApiStandardErrors.SC404_03_NOTIFICATION_NOT_FOUND,
                "Resource with ID [17] not found"
        );
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateEventNotification_Error422xx() {

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

        // 42204 - Notification is invalid - Notification Event object cannot be null
        eventToDispatch = notification.getEvent();
        notification.setEvent(null);
        ResponseEntity<ObjectNode> putResponse = notificationClient.updateEventNotificationResponseEntity(
                notification.getId(), notification
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_04_NOTIFICATION_IS_INVALID,
                "Notification Event object cannot be null"
        );

        // 42204 - Notification is invalid - Notification Observer object cannot be null
        notification.setEvent(eventToDispatch);
        observerResource = notification.getObserver();
        notification.setObserver(null);
        putResponse = notificationClient.updateEventNotificationResponseEntity(
                notification.getId(), notification
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_04_NOTIFICATION_IS_INVALID,
                "Notification Observer object cannot be null"
        );

        // 42204 - Notification is invalid - Notification Status cannot be null
        notification.setObserver(observerResource);
        notification.setStatus(null);
        putResponse = notificationClient.updateEventNotificationResponseEntity(
                notification.getId(), notification
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_04_NOTIFICATION_IS_INVALID,
                "Notification Status cannot be null"
        );

    }


    // ======================================================================================
    // READ ALL EventNotifications
    // ======================================================================================

    // No specific errors except for search errors


    // ======================================================================================
    // READ ONE EventNotification
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneEventNotification_Error404xx() {

        // 40403 - Notification not found - Resource with ID [id] not found

        // GET request
        ResponseEntity<ObjectNode> getResponse = notificationClient.readOneEventNotificationResponseEntity(1L);
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                NotificationApiStandardErrors.SC404_03_NOTIFICATION_NOT_FOUND,
                "Resource with ID [1] not found"
        );

    }

}