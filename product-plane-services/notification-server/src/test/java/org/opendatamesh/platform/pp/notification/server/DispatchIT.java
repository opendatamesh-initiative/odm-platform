package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DispatchIT extends ODMNotificationIT {

    // ======================================================================================
    // DISPATCH Event (POST)
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDispatchEvent() {

        // Resources + Creation
        createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        EventResource eventToDispatch = createEventResource(ODMNotificationResources.RESOURCE_EVENT);

        // POST request
        notifyEvent(eventToDispatch);

        // Check event
        ResponseEntity<ObjectNode> getResponse = notificationClient.searchEventsResponseEntity();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<EventResource> events = extractListFromPageFromObjectNode(getResponse.getBody(), EventResource.class);
        assertThat(events.size()).isEqualTo(1);
        eventToDispatch.setId(events.get(0).getId());
        assertThat(events.get(0)).usingRecursiveComparison().isEqualTo(eventToDispatch);

        // Check notification
        getResponse = notificationClient.searchEventNotificationsResponseEntity(new EventNotificationSearchOptions());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<EventNotificationResource> notifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(notifications.size()).isEqualTo(1);
        assertThat(notifications.get(0).getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch);

        // Check interactions with Observer
        Mockito.verify(notificationObserverServiceProxyMock, Mockito.times(1))
                .dispatchEventNotificationToObserver(Mockito.any(), Mockito.any());

    }

}