package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.server.services.proxies.NotificationObserverServiceProxy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DispatchIT extends ODMNotificationIT {

    @MockBean
    private NotificationObserverServiceProxy notificationObserverServiceProxyMock;


    // ======================================================================================
    // DISPATCH Event (POST)
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDispatchEvent() throws IOException {

        // Resources + Creation
        createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        EventResource eventToDispatch = resourceBuilder.readResourceFromFile(
                ODMNotificationResources.RESOURCE_EVENT, EventResource.class
        );


        // Mock communication with Observers
        Mockito.doNothing()
                .when(notificationObserverServiceProxyMock)
                .dispatchEventNotificationToObserver(
                        Mockito.any(EventNotificationResource.class),
                        Mockito.any(ObserverResource.class)
                );

        // POST request
        ResponseEntity<ObjectNode> postResponse = notificationClient.notifyEventResponseEntity(eventToDispatch);
        verifyResponseEntity(postResponse, HttpStatus.OK, false);

        // Check notification
        ResponseEntity<ObjectNode> getResponse =
                notificationClient.searchEventNotificationsResponseEntity(new EventNotificationSearchOptions());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<EventNotificationResource> notifications = extractListFromPageFromObjectNode(
                getResponse.getBody(), EventNotificationResource.class
        );
        assertThat(notifications.size()).isEqualTo(1);
        assertThat(notifications.get(0).getEvent()).usingRecursiveComparison().isEqualTo(eventToDispatch);

        Mockito.verify(notificationObserverServiceProxyMock, Mockito.times(1))
                .dispatchEventNotificationToObserver(Mockito.any(), Mockito.any());

    }

}
