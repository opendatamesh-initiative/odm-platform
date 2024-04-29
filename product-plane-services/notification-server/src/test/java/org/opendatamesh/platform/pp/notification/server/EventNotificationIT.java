package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

public class EventNotificationIT extends ODMNotificationIT {

    // ======================================================================================
    // UPDATE EventNotification
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateEventNotification() {

        // Dispatch an Event to force EventNotification creation
/*
        // Resources + Creation
        EventNotificationResource eventNotificationResource = createEventNotification(ODMEventNotifierResources.RESOURCE_OBSERVER_1);
        EventNotificationResource eventNotificationResourceUpdated = createEventNotificationResource(ODMEventNotifierResources.RESOURCE_OBSERVER_1_UPDATED);
        // TODO: discuss update strategies (ID and CreationTime actually MUST be in the updated object)
        eventNotificationResourceUpdated.setId(eventNotificationResource.getId());
        //eventNotificationResourceUpdated.set(eventNotificationResource.getCreatedAt());

        // PUT request
        ResponseEntity<ObjectNode> putResponse = eventNotifierClient.updateEventNotificationResponseEntity(
                eventNotificationResource.getId(),
                eventNotificationResourceUpdated
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        eventNotificationResourceUpdated = mapper.convertValue(putResponse.getBody(), EventNotificationResource.class);

        // Verification
        verifyResourceEventNotificationOneUpdated(eventNotificationResourceUpdated);
*/
    }


    // ======================================================================================
    // READ ALL EventNotifications
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllEventNotifications() {
/*
        // Resources + Creation
        createEventNotification(ODMEventNotifierResources.RESOURCE_OBSERVER_1);
        createEventNotification(ODMEventNotifierResources.RESOURCE_OBSERVER_2);

        // GET request
        ResponseEntity<ObjectNode> getResponse = eventNotifierClient.searchEventNotificationsResponseEntity(null);
        verifyResponseEntity(getResponse, HttpStatus.OK, true);

        // Verification
        List<EventNotificationResource> eventNotifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(eventNotifications).size().isEqualTo(2);
        verifyResourceEventNotificationOne(eventNotifications.get(0));
        verifyResourceEventNotificationTwo(eventNotifications.get(1));
*/
    }


    // ======================================================================================
    // READ ONE EventNotification
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneEventNotification() throws JsonProcessingException {
/*
        // Resources + Creation
        EventNotificationResource eventNotificationResource = createEventNotification(ODMEventNotifierResources.RESOURCE_OBSERVER_1);

        // GET request
        ResponseEntity<ObjectNode> getResponse = eventNotifierClient.readOneEventNotificationResponseEntity(eventNotificationResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        eventNotificationResource = mapper.convertValue(getResponse.getBody(), EventNotificationResource.class);

        // Verification
        verifyResourceEventNotificationOne(eventNotificationResource);
*/
    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourceEventNotificationOne(EventNotificationResource eventNotificationResource) {
        assertThat(eventNotificationResource.getId()).isNotNull();
        assertThat(eventNotificationResource.getEvent()).isNotNull();
    }

    private void verifyResourceEventNotificationOneUpdated(EventNotificationResource eventNotificationResource) {
        assertThat(eventNotificationResource.getId()).isNotNull();
        assertThat(eventNotificationResource.getEvent()).isNotNull();
    }

    private void verifyResourceEventNotificationTwo(EventNotificationResource eventNotificationResource) {
        assertThat(eventNotificationResource.getId()).isNotNull();
        assertThat(eventNotificationResource.getEvent()).isNotNull();
    }
    
}
