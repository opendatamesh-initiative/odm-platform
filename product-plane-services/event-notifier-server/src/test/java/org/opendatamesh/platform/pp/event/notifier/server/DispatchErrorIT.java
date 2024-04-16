package org.opendatamesh.platform.pp.event.notifier.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.event.notifier.api.resources.exceptions.EventNotifierApiStandardErrors;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

public class DispatchErrorIT extends ODMEventNotifierIT {

    // ======================================================================================
    // DISPATCH Event (POST)
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDispatchEventError400xx() throws IOException {

        // 40001 - Empty Observer
        ResponseEntity<ObjectNode> postResponse = eventNotifierClient.notifyEventResponseEntity(null);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.BAD_REQUEST,
                EventNotifierApiStandardErrors.SC400_02_EVENT_IS_EMPTY,
                "Event object cannot be null"
        );

    }

}