package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

public class EventErrorIT extends ODMNotificationIT {

    // ======================================================================================
    // READ ALL Events
    // ======================================================================================

    // No specific errors except for search errors


    // ======================================================================================
    // READ ONE Event
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneEvent_Error404xx() {

        // 40403 - Notification not found - Resource with ID [id] not found

        // GET request
        ResponseEntity<ObjectNode> getResponse = notificationClient.readOneEventResponseEntity(1L);
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                NotificationApiStandardErrors.SC404_02_EVENT_NOT_FOUND,
                "Resource with ID [1] not found"
        );

    }

}
