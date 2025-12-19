package org.opendatamesh.platform.pp.policy.server.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PolicyEngine error handling.
 * These tests verify that appropriate error responses are returned for invalid PolicyEngine operations.
 */
public class PolicyEngineErrorsIT extends PolicyApplicationIT {

    /**
     * Given: No policy engine resource is provided (null)
     * When: A policy engine creation request is made with null policy engine
     * Then: A 400 BAD_REQUEST error is returned
     * And: The error code is SC400_01_POLICY_ENGINE_IS_EMPTY
     * And: The error message indicates that the policy engine object cannot be null
     */
    @Test
    public void whenCreatePolicyEngineWithNullThenReturnBadRequest() {
        // Given
        // No policy engine resource is provided (null)

        // When
        ResponseEntity<ErrorRes> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(null),
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC400_01_POLICY_ENGINE_IS_EMPTY.code());
        assertThat(response.getBody().getDescription()).isEqualTo(PolicyApiStandardErrors.SC400_01_POLICY_ENGINE_IS_EMPTY.description());
    }

    /**
     * Given: A policy engine
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_01_POLICY_ENGINE_IS_INVALID
     * And: The error message indicates that policy engine adapterUrl cannot be null
     * When: A policy engine creation request is made with null name
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that policy engine name cannot be null
     * When: A policy engine creation request is made with a name that already exists
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_05_POLICY_ENGINE_ALREADY_EXISTS
     * And: The error message indicates that a policy engine with the same name already exists
     */
    @Test
    public void whenCreatePolicyEngineWithInvalidDataThenReturnUnprocessableEntity() {
        // Given
        PolicyEngineResource policyEngineWithoutAdapterUrl = new PolicyEngineResource();
        policyEngineWithoutAdapterUrl.setName("opa-policy-checker");
        policyEngineWithoutAdapterUrl.setDisplayName("OPA Policy Checker");

        // When
        ResponseEntity<ErrorRes> response1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngineWithoutAdapterUrl),
                ErrorRes.class
        );

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response1.getBody()).isNotNull();
        if (response1.getBody() != null) {
            assertThat(response1.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID.code());
        }

        // Given
        PolicyEngineResource policyEngineWithoutName = new PolicyEngineResource();
        policyEngineWithoutName.setDisplayName("OPA Policy Checker");
        policyEngineWithoutName.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        // When
        ResponseEntity<ErrorRes> response2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngineWithoutName),
                ErrorRes.class
        );

        // Then
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response2.getBody()).isNotNull();

        // Given
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName("opa-policy-checker");
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        PolicyEngineResource createdEngine = createResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();

        PolicyEngineResource duplicatePolicyEngine = new PolicyEngineResource();
        duplicatePolicyEngine.setName("opa-policy-checker");
        duplicatePolicyEngine.setDisplayName("OPA Policy Checker 2");
        duplicatePolicyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator-2");

        // When
        ResponseEntity<ErrorRes> response3 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(duplicatePolicyEngine),
                ErrorRes.class
        );

        // Then
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response3.getBody()).isNotNull();
        assertThat(response3.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS.code());

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine resource is created
     * When: A policy engine update request is made with null policy engine
     * Then: A 400 BAD_REQUEST error is returned
     * And: The error code is SC400_01_POLICY_ENGINE_IS_EMPTY
     * And: The error message indicates that the policy engine object cannot be null
     */
    @Test
    public void whenUpdatePolicyEngineWithNullThenReturnBadRequest() {
        // Given
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName("opa-policy-checker");
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        PolicyEngineResource createdEngine = createResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();

        // When
        ResponseEntity<ErrorRes> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.PUT,
                new HttpEntity<>(null),
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC400_01_POLICY_ENGINE_IS_EMPTY.code());

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine
     * When: A policy engine update request is made with a non-existent ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_01_POLICY_ENGINE_NOT_FOUND
     * And: The error message indicates that the resource with the specified ID was not found
     */
    @Test
    public void whenUpdatePolicyEngineWithNonExistentIdThenReturnNotFound() {
        // Given
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName("opa-policy-checker");
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        // When
        ResponseEntity<ErrorRes> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/99999",
                HttpMethod.PUT,
                new HttpEntity<>(policyEngine),
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND.code());
    }

    /**
     * Given: A policy engine resource is created
     * When: A policy engine update request is made with null adapterUrl
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_01_POLICY_ENGINE_IS_INVALID
     * And: The error message indicates that policy engine adapterUrl cannot be null
     * When: A policy engine update request is made with null name
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that policy engine name cannot be null
     */
    @Test
    public void whenUpdatePolicyEngineWithInvalidDataThenReturnUnprocessableEntity() {
        // Given
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
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

        PolicyEngineResource invalidPolicyEngine1 = new PolicyEngineResource();
        invalidPolicyEngine1.setId(engineId);
        invalidPolicyEngine1.setName(uniqueEngineName);
        invalidPolicyEngine1.setDisplayName("OPA Policy Checker");
        invalidPolicyEngine1.setCreatedAt(createdAt);

        // When
        ResponseEntity<ErrorRes> response1 = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidPolicyEngine1),
                ErrorRes.class
        );

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response1.getBody()).isNotNull();

        assertThat(response1.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID.code());


        PolicyEngineResource invalidPolicyEngine2 = new PolicyEngineResource();
        invalidPolicyEngine2.setId(engineId);
        invalidPolicyEngine2.setDisplayName("OPA Policy Checker");
        invalidPolicyEngine2.setAdapterUrl("http://localhost:9001/api/v1/up/validator");
        invalidPolicyEngine2.setCreatedAt(createdAt);

        // When
        ResponseEntity<ErrorRes> response2 = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidPolicyEngine2),
                ErrorRes.class
        );

        // Then
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response2.getBody()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: No policy engine exists with the specified ID
     * When: A policy engine retrieval request is made with a non-existent ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_01_POLICY_ENGINE_NOT_FOUND
     * And: The error message indicates that the resource with the specified ID was not found
     */
    @Test
    public void whenReadOnePolicyEngineWithNonExistentIdThenReturnNotFound() {
        // Given
        // No policy engine exists with the specified ID

        // When
        ResponseEntity<ErrorRes> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/99999",
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND.code());

    }

    /**
     * Given: No policy engine exists with the specified ID
     * When: A policy engine deletion request is made with a non-existent ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_01_POLICY_ENGINE_NOT_FOUND
     * And: The error message indicates that the resource with the specified ID was not found
     */
    @Test
    public void whenDeletePolicyEngineWithNonExistentIdThenReturnNotFound() {
        // Given
        // No policy engine exists with the specified ID

        // When
        ResponseEntity<ErrorRes> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/99999",
                HttpMethod.DELETE,
                null,
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND.code());

    }

    /**
     * Given: A first policy engine is created with name "opa-policy-checker"
     * And: A second policy engine is created with a different name
     * When: An attempt is made to update the second policy engine to use the same name as the first
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_05_POLICY_ENGINE_ALREADY_EXISTS
     * And: The error message indicates that a policy engine with that name already exists
     */
    @Test
    public void whenUpdatePolicyEngineToExistingNameThenReturnUnprocessableEntity() {
        // Given
        String uniqueEngineName1 = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEngineName2 = "lambda-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine1 = new PolicyEngineResource();
        policyEngine1.setName(uniqueEngineName1);
        policyEngine1.setDisplayName("OPA Policy Checker");
        policyEngine1.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

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

        PolicyEngineResource policyEngine2 = new PolicyEngineResource();
        policyEngine2.setName(uniqueEngineName2);
        policyEngine2.setDisplayName("Lambda Policy Checker");
        policyEngine2.setAdapterUrl("http://localhost:9001/api/v1/up/validator-2");

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
        Date createdAt2 = createdEngine2.getCreatedAt();

        PolicyEngineResource updatedPolicyEngine2 = new PolicyEngineResource();
        updatedPolicyEngine2.setId(engineId2);
        updatedPolicyEngine2.setName(uniqueEngineName1);
        updatedPolicyEngine2.setDisplayName("Lambda Policy Checker");
        updatedPolicyEngine2.setAdapterUrl("http://localhost:9001/api/v1/up/validator-2");
        updatedPolicyEngine2.setCreatedAt(createdAt2);

        // When
        ResponseEntity<ErrorRes> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId2,
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicyEngine2),
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS.code());


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
}
