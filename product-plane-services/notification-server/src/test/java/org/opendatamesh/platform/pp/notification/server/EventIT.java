package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventIT extends ODMNotificationIT {

    // ======================================================================================
    // READ ALL Events
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllEvents() {

        // 1 Observer, 2 Events

        // Resources + Creation
        createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        EventResource eventToDispatch = createEventResource(ODMNotificationResources.RESOURCE_EVENT);
        EventResource eventToDispatch2 = createEventResource(ODMNotificationResources.RESOURCE_EVENT_2);
        notifyEvent(eventToDispatch);
        notifyEvent(eventToDispatch2);

        // GET request
        ResponseEntity<ObjectNode> getResponse = notificationClient.searchEventsResponseEntity();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<EventResource> events = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventResource.class
        );
        assertThat(events.size()).isEqualTo(2);
        eventToDispatch.setId(1L);
        assertThat(events.get(0)).usingRecursiveComparison().isEqualTo(eventToDispatch);
        eventToDispatch2.setId(2L);
        assertThat(events.get(1)).usingRecursiveComparison().isEqualTo(eventToDispatch2);

    }


    // ======================================================================================
    // READ ONE Event
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneEvent() {

        // Resources + Creation
        createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        EventResource eventToDispatch = createEventResource(ODMNotificationResources.RESOURCE_EVENT);
        notifyEvent(eventToDispatch);

        // GET request
        ResponseEntity<ObjectNode> getResponse = notificationClient.readOneEventResponseEntity(1L);
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        EventResource event = mapper.convertValue(getResponse.getBody(), EventResource.class);

        eventToDispatch.setId(1L);
        assertThat(event).usingRecursiveComparison().isEqualTo(eventToDispatch);

    }

}
