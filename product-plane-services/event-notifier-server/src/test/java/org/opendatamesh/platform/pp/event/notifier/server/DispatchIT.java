package org.opendatamesh.platform.pp.event.notifier.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;

public class DispatchIT extends ODMEventNotifierIT {


    // ======================================================================================
    // DISPATCH Event (POST)
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDispatchEvent() throws IOException {

        // Resources + Creation
        EventResource eventToDispatch = resourceBuilder.readResourceFromFile(
                ODMEventNotifierResources.RESOURCE_EVENT, EventResource.class
        );

        // POST request
        ResponseEntity<ObjectNode> postResponse = eventNotifierClient.notifyEventResponseEntity(eventToDispatch);
        verifyResponseEntity(postResponse, HttpStatus.OK, false);

    }

}