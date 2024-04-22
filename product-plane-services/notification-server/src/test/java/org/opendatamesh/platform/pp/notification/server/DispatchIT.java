package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

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
