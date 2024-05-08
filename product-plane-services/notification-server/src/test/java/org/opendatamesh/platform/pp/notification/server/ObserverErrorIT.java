package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

public class ObserverErrorIT extends ODMNotificationIT {

    // ======================================================================================
    // CREATE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateObserverError400xx() {

        // 40001 - Empty Observer
        ResponseEntity<ObjectNode> postResponse = notificationClient.addObserverResponseEntity(null);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.BAD_REQUEST,
                NotificationApiStandardErrors.SC400_01_OBSERVER_IS_EMPTY,
                "Observer object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateObserverError422xx() {

        // Resources
        ObserverResource observerResource = createObserverResource(ODMNotificationResources.RESOURCE_OBSERVER_1);
        String observerServerAddress = observerResource.getObserverServerBaseUrl();
        ResponseEntity<ObjectNode> postResponse;

        // 42201 - Observer is invalid - Observer server address cannot be null
        observerResource.setObserverServerBaseUrl(null);
        postResponse = notificationClient.addObserverResponseEntity(observerResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_01_OBSERVER_IS_INVALID,
                "Observer server base URL cannot be null"
        );

        // 42201 - Observer is invalid - Observer name cannot be null
        observerResource.setObserverServerBaseUrl(observerServerAddress);
        observerResource.setName(null);
        postResponse = notificationClient.addObserverResponseEntity(observerResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_01_OBSERVER_IS_INVALID,
                "Observer name cannot be null"
        );

        // 42205 - Observer is invalid - Observer with name [" + observerResource.getName() + "] already exists
        observerResource = createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        observerResource.setId(null);
        postResponse = notificationClient.addObserverResponseEntity(observerResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_02_OBSERVER_ALREADY_EXISTS,
                "Observer with name [" + observerResource.getName() + "] already exists"
        );

    }

    // ======================================================================================
    // UPDATE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateObserverError400xx() {

        // Resources
        ObserverResource observerResource = createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);

        // 40001 - Empty Observer
        ResponseEntity<ObjectNode> putResponse = notificationClient.updateObserverResponseEntity(observerResource.getId(), null);
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.BAD_REQUEST,
                NotificationApiStandardErrors.SC400_01_OBSERVER_IS_EMPTY,
                "Observer object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateObserverError404xx() {

        // Resources
        ObserverResource observerResource = createObserverResource(ODMNotificationResources.RESOURCE_OBSERVER_1);

        // 40401 - Observer not found
        ResponseEntity<ObjectNode> putResponse = notificationClient.updateObserverResponseEntity(2L, observerResource);
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.NOT_FOUND,
                NotificationApiStandardErrors.SC404_01_OBSERVER_NOT_FOUND,
                "Observer with ID [2] not found"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateObserverError422xx() {

        // Resources
        ObserverResource observerResource = createObserver(ODMNotificationResources.RESOURCE_OBSERVER_1);
        String observerServerAddress = observerResource.getObserverServerBaseUrl();
        ResponseEntity<ObjectNode> putResponse;

        // 42201 - Observer is invalid - Observer server address cannot be null
        observerResource.setObserverServerBaseUrl(null);
        putResponse = notificationClient.updateObserverResponseEntity(observerResource.getId(), observerResource);
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_01_OBSERVER_IS_INVALID,
                "Observer server base URL cannot be null"
        );

        // 42201 - Observer is invalid - Observer name cannot be null
        observerResource.setObserverServerBaseUrl(observerServerAddress);
        observerResource.setName(null);
        putResponse = notificationClient.updateObserverResponseEntity(observerResource.getId(), observerResource);
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                NotificationApiStandardErrors.SC422_01_OBSERVER_IS_INVALID,
                "Observer name cannot be null"
        );

    }


    // ======================================================================================
    // READ ALL Observers
    // ======================================================================================

    // No specific errors to test excluding 500

    // ======================================================================================
    // READ ONE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneObserverError404xx() {

        // 40401 - Resource not found
        ResponseEntity<ObjectNode> getResponse = notificationClient.getObserverResponseEntity(2L);
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                NotificationApiStandardErrors.SC404_01_OBSERVER_NOT_FOUND,
                "Observer with ID [2] not found"
        );

    }

    // ======================================================================================
    // DELETE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteOneObserverError404xx() {

        // 40401 - Resource not found
        ResponseEntity<ObjectNode> deleteResponse = notificationClient.removeObserverResponseEntity(2L);
        verifyResponseErrorObjectNode(
                deleteResponse,
                HttpStatus.NOT_FOUND,
                NotificationApiStandardErrors.SC404_01_OBSERVER_NOT_FOUND,
                "Observer with ID [2] not found"
        );

    }

}
