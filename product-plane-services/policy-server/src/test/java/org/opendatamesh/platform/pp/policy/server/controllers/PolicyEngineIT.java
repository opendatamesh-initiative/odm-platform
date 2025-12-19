package org.opendatamesh.platform.pp.policy.server.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.server.client.utils.jackson.PageUtility;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PolicyEngine resource operations.
 * These tests verify CRUD operations and business logic for PolicyEngine resources.
 */
public class PolicyEngineIT extends PolicyApplicationIT {

    /**
     * Given: A policy engine resource is created from a JSON file
     * When: The policy engine is created via API
     * Then: The policy engine is successfully created
     * And: The engine has correct ID, name, displayName, adapterUrl, and timestamps
     */
    @Test
    public void whenCreatePolicyEngineThenReturnCreatedPolicyEngine() {
        // Given
        String uniqueName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        // When
        ResponseEntity<PolicyEngineResource> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        PolicyEngineResource createdEngine = response.getBody();
        assertThat(createdEngine.getId()).isNotNull();
        assertThat(createdEngine.getName()).isEqualTo(uniqueName);
        assertThat(createdEngine.getDisplayName()).isEqualTo("OPA Policy Checker");
        assertThat(createdEngine.getAdapterUrl()).isEqualTo("http://localhost:9001/api/v1/up/validator");
        assertThat(createdEngine.getCreatedAt()).isNotNull();
        assertThat(createdEngine.getUpdatedAt()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + createdEngine.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine resource is created
     * And: An updated policy engine
     * When: The policy engine is updated via PUT request
     * Then: The policy engine is successfully updated
     * And: The updated engine has the new values for displayName and adapterUrl
     * And: The updatedAt timestamp is after the createdAt timestamp
     */
    @Test
    public void whenUpdatePolicyEngineThenReturnUpdatedPolicyEngine() {
        // Given
        String uniqueName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = createResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();
        Date createdAt = createdEngine.getCreatedAt();

        PolicyEngineResource updatedPolicyEngine = new PolicyEngineResource();
        updatedPolicyEngine.setId(engineId);
        updatedPolicyEngine.setName(uniqueName);
        updatedPolicyEngine.setDisplayName("OPA Policy Checker V2");
        updatedPolicyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator-2");
        updatedPolicyEngine.setCreatedAt(createdAt);

        // When
        ResponseEntity<PolicyEngineResource> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicyEngine),
                PolicyEngineResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyEngineResource updatedEngine = response.getBody();
        assertThat(updatedEngine.getId()).isEqualTo(engineId);
        assertThat(updatedEngine.getDisplayName()).isEqualTo("OPA Policy Checker V2");
        assertThat(updatedEngine.getAdapterUrl()).isEqualTo("http://localhost:9001/api/v1/up/validator-2");
        assertThat(updatedEngine.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedEngine.getUpdatedAt()).isNotNull();
        assertThat(updatedEngine.getUpdatedAt()).isNotEqualTo(createdAt);

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: Multiple policy engines are created
     * When: All policy engines are retrieved via GET request
     * Then: All policy engines are returned in the response
     * And: Each engine has the expected properties
     */
    @Test
    public void whenReadAllPolicyEnginesThenReturnAllPolicyEngines() {
        // Given
        String uniqueName1 = "opa-policy-checker-1-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueName2 = "opa-policy-checker-2-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine1 = new PolicyEngineResource();
        policyEngine1.setName(uniqueName1);
        policyEngine1.setDisplayName("OPA Policy Checker 1");
        policyEngine1.setAdapterUrl("http://localhost:9001/api/v1/up/validator-1");

        PolicyEngineResource policyEngine2 = new PolicyEngineResource();
        policyEngine2.setName(uniqueName2);
        policyEngine2.setDisplayName("OPA Policy Checker 2");
        policyEngine2.setAdapterUrl("http://localhost:9001/api/v1/up/validator-2");

        ResponseEntity<PolicyEngineResource> createResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine1),
                PolicyEngineResource.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine1 = createResponse1.getBody();
        assertThat(createdEngine1).isNotNull();
        Long engineId1 = createdEngine1.getId();
        assertThat(engineId1).isNotNull();

        ResponseEntity<PolicyEngineResource> createResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine2),
                PolicyEngineResource.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine2 = createResponse2.getBody();
        assertThat(createdEngine2).isNotNull();
        Long engineId2 = createdEngine2.getId();
        assertThat(engineId2).isNotNull();

        // When
        ResponseEntity<PageUtility<PolicyEngineResource>> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<PolicyEngineResource>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PageUtility<PolicyEngineResource> page = response.getBody();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);
        boolean foundEngine1 = page.getContent().stream()
                .anyMatch(e -> e.getId().equals(engineId1) && e.getName().equals(uniqueName1));
        boolean foundEngine2 = page.getContent().stream()
                .anyMatch(e -> e.getId().equals(engineId2) && e.getName().equals(uniqueName2));
        assertThat(foundEngine1).isTrue();
        assertThat(foundEngine2).isTrue();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId1,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId2,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine resource is created
     * When: The policy engine is retrieved by ID via GET request
     * Then: The policy engine is successfully retrieved
     * And: The engine has all expected fields and properties
     */
    @Test
    public void whenReadOnePolicyEngineThenReturnPolicyEngine() {
        // Given
        String uniqueName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = createResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        // When
        ResponseEntity<PolicyEngineResource> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                PolicyEngineResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyEngineResource retrievedEngine = response.getBody();
        assertThat(retrievedEngine.getId()).isEqualTo(engineId);
        assertThat(retrievedEngine.getName()).isEqualTo(uniqueName);
        assertThat(retrievedEngine.getDisplayName()).isEqualTo("OPA Policy Checker");
        assertThat(retrievedEngine.getAdapterUrl()).isEqualTo("http://localhost:9001/api/v1/up/validator");
        assertThat(retrievedEngine.getCreatedAt()).isNotNull();
        assertThat(retrievedEngine.getUpdatedAt()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

    }

    // ======================================================================================
    // DELETE PolicyEngine
    // ======================================================================================

    /**
     * Given: A policy engine resource is created
     * When: The policy engine is deleted via DELETE request
     * Then: The policy engine is successfully deleted
     * And: Attempting to retrieve the policy engine returns a 404 NOT_FOUND error
     */
    @Test
    public void whenDeletePolicyEngineThenPolicyEngineIsDeleted() {
        // Given
        String uniqueName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = createResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        // When
        ResponseEntity<Void> deleteResponse = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<PolicyEngineResource> getResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                PolicyEngineResource.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
