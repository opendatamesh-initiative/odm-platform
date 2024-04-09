package org.opendatamesh.platform.pp.event.notifier.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.event.notifier.api.resources.PagedObserverResource;
import org.opendatamesh.platform.pp.event.notifier.api.resources.exceptions.EventNotifierApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ObserverIT extends ODMEventNotifierIT {

    // ======================================================================================
    // CREATE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateObserver() {

        // Resources + Creation
        ObserverResource observerResource = createObserver(ODMEventNotifierResources.RESOURCE_OBSERVER_1);

        // Verification
        verifyResourceObserverOne(observerResource);

    }

    // ======================================================================================
    // UPDATE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateObserver() {

        // Resources + Creation
        ObserverResource observerResource = createObserver(ODMEventNotifierResources.RESOURCE_OBSERVER_1);
        ObserverResource observerResourceUpdated = createObserverResource(ODMEventNotifierResources.RESOURCE_OBSERVER_1_UPDATED);
        // TODO: discuss update strategies (ID and CreationTime actually MUST be in the updated object)
        observerResourceUpdated.setId(observerResource.getId());
        observerResourceUpdated.setCreatedAt(observerResource.getCreatedAt());

        // PUT request
        ResponseEntity<ObjectNode> putResponse = eventNotifierClient.updateObserverResponseEntity(
                observerResource.getId(),
                observerResourceUpdated
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        observerResourceUpdated = mapper.convertValue(putResponse.getBody(), ObserverResource.class);

        // Verification
        verifyResourceObserverOneUpdated(observerResourceUpdated);

    }


    // ======================================================================================
    // READ ALL Observers
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllObservers() {

        // Resources + Creation
        createObserver(ODMEventNotifierResources.RESOURCE_OBSERVER_1);
        createObserver(ODMEventNotifierResources.RESOURCE_OBSERVER_2);

        // GET request
        ResponseEntity<ObjectNode> getResponse = eventNotifierClient.getObserversResponseEntity();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<ObserverResource> observers = mapper.convertValue(getResponse.getBody(), PagedObserverResource.class).getContent();

        // Verification
        assertThat(observers).size().isEqualTo(2);
        verifyResourceObserverOne(observers.get(0));
        verifyResourceObserverTwo(observers.get(1));

    }


    // ======================================================================================
    // READ ONE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneObserver() throws JsonProcessingException {

        // Resources + Creation
        ObserverResource observerResource = createObserver(ODMEventNotifierResources.RESOURCE_OBSERVER_1);

        // GET request
        ResponseEntity<ObjectNode> getResponse = eventNotifierClient.getObserverResponseEntity(observerResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        observerResource = mapper.convertValue(getResponse.getBody(), ObserverResource.class);

        // Verification
        verifyResourceObserverOne(observerResource);

    }


    // ======================================================================================
    // DELETE Observer
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteObserver() throws JsonProcessingException {

        // Resources + Creation
        ObserverResource observerResource = createObserver(ODMEventNotifierResources.RESOURCE_OBSERVER_1);

        // DELETE request
        ResponseEntity deleteResponse = eventNotifierClient.removeObserverResponseEntity(observerResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ObjectNode> getResponse = eventNotifierClient.getObserverResponseEntity(observerResource.getId());
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                EventNotifierApiStandardErrors.SC404_01_OBSERVER_NOT_FOUND,
                "Observer with ID [" + observerResource.getId() + "] not found"
        );

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourceObserverOne(ObserverResource observerResource) {

        assertThat(observerResource.getId()).isNotNull();
        assertThat(observerResource.getName()).isEqualTo("opa-policy-checker");
        assertThat(observerResource.getDisplayName()).isEqualTo("OPA Policy Checker");
        assertThat(observerResource.getObserverServerAddress()).isEqualTo("http://localhost:9001/api/v1/up/policy-engine-adapter");
        assertThat(observerResource.getCreatedAt()).isNotNull();
        assertThat(observerResource.getUpdatedAt()).isEqualTo(observerResource.getCreatedAt());

    }

    private void verifyResourceObserverOneUpdated(ObserverResource observerResource) {

        assertThat(observerResource.getId()).isNotNull();
        assertThat(observerResource.getName()).isEqualTo("opa-policy-checker");
        assertThat(observerResource.getDisplayName()).isEqualTo("OPA Policy Checker V2");
        assertThat(observerResource.getObserverServerAddress()).isEqualTo("http://localhost:9001/api/v1/up/policy-engine-adapter-2");
        assertThat(observerResource.getCreatedAt()).isNotNull();
        assertThat(observerResource.getUpdatedAt()).isAfter(observerResource.getCreatedAt());

    }

    private void verifyResourceObserverTwo(ObserverResource observerResource) {

        assertThat(observerResource.getId()).isNotNull();
        assertThat(observerResource.getName()).isEqualTo("lambda-policy-checker");
        assertThat(observerResource.getDisplayName()).isEqualTo("Custom Lambda Policy Checker");
        assertThat(observerResource.getObserverServerAddress()).isEqualTo("https://abcdefg.lambda-url.us-east-1.on.aws");
        assertThat(observerResource.getCreatedAt()).isNotNull();
        assertThat(observerResource.getUpdatedAt()).isEqualTo(observerResource.getCreatedAt());

    }

}
