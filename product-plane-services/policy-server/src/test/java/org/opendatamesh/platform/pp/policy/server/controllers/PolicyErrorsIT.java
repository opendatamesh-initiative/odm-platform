package org.opendatamesh.platform.pp.policy.server.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationEventResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Policy error handling.
 * These tests verify that appropriate error responses are returned for invalid Policy operations.
 */
public class PolicyErrorsIT extends PolicyApplicationIT {

    /**
     * Given: No policy resource is provided (null)
     * When: A policy creation request is made with null policy
     * Then: A 400 BAD_REQUEST error is returned
     * And: The error code is SC400_02_POLICY_IS_EMPTY
     * And: The error message indicates that the policy object cannot be null
     */
    @Test
    public void whenCreatePolicyWithNullThenReturnBadRequest() {
        // Given
        // No policy resource is provided (null)

        // When
        ResponseEntity<ErrorRes> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(null),
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        if (response.getBody() != null) {
            assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC400_02_POLICY_IS_EMPTY.code());
            assertThat(response.getBody().getDescription()).isEqualTo(PolicyApiStandardErrors.SC400_02_POLICY_IS_EMPTY.description());
        }
    }

    /**
     * Given: A policy engine exists
     * When: A policy creation request is made with an explicit ID
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_02_POLICY_IS_INVALID
     * And: The error message indicates that a policy cannot be created with an explicit ID
     * When: A policy creation request is made with an explicit rootId
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that a policy cannot be created with an explicit rootId
     * When: A policy creation request is made with null name
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that policy name cannot be null
     * When: A policy creation request is made with null policyEngine
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that policyEngineId or PolicyEngine object cannot be null
     * When: A policy creation request is made with a name that already exists
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_04_POLICY_ALREADY_EXISTS
     * And: The error message indicates that a policy with the same name already exists
     */
    @Test
    public void whenCreatePolicyWithInvalidDataThenReturnUnprocessableEntity() {
        // Given
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName("opa-policy-checker");
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();

        PolicyResource policyWithId = new PolicyResource();
        policyWithId.setId(1L);
        policyWithId.setName("dataproduct-name-checker");
        policyWithId.setDisplayName("Data Product Name Checker");
        policyWithId.setBlockingFlag(true);
        policyWithId.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef1 = new PolicyEngineResource();
        policyEngineRef1.setId(engineId);
        policyEngineRef1.setName("opa-policy-checker");
        policyWithId.setPolicyEngine(policyEngineRef1);

        List<PolicyEvaluationEventResource> evaluationEvents1 = new ArrayList<>();
        PolicyEvaluationEventResource event1 = new PolicyEvaluationEventResource();
        event1.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents1.add(event1);
        policyWithId.setEvaluationEvents(evaluationEvents1);

        // When
        ResponseEntity<ErrorRes> response1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policyWithId),
                ErrorRes.class
        );

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response1.getBody()).isNotNull();
        if (response1.getBody() != null) {
            assertThat(response1.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID.code());
        }

        PolicyResource policyWithRootId = new PolicyResource();
        policyWithRootId.setRootId(1L);
        policyWithRootId.setName("dataproduct-name-checker");
        policyWithRootId.setDisplayName("Data Product Name Checker");
        policyWithRootId.setBlockingFlag(true);
        policyWithRootId.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef2 = new PolicyEngineResource();
        policyEngineRef2.setId(engineId);
        policyEngineRef2.setName("opa-policy-checker");
        policyWithRootId.setPolicyEngine(policyEngineRef2);

        List<PolicyEvaluationEventResource> evaluationEvents2 = new ArrayList<>();
        PolicyEvaluationEventResource event2 = new PolicyEvaluationEventResource();
        event2.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents2.add(event2);
        policyWithRootId.setEvaluationEvents(evaluationEvents2);

        // When
        ResponseEntity<ErrorRes> response2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policyWithRootId),
                ErrorRes.class
        );

        // Then
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response2.getBody()).isNotNull();

        PolicyResource policyWithoutName = new PolicyResource();
        policyWithoutName.setDisplayName("Data Product Name Checker");
        policyWithoutName.setBlockingFlag(true);
        policyWithoutName.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef3 = new PolicyEngineResource();
        policyEngineRef3.setId(engineId);
        policyEngineRef3.setName("opa-policy-checker");
        policyWithoutName.setPolicyEngine(policyEngineRef3);

        List<PolicyEvaluationEventResource> evaluationEvents3 = new ArrayList<>();
        PolicyEvaluationEventResource event3 = new PolicyEvaluationEventResource();
        event3.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents3.add(event3);
        policyWithoutName.setEvaluationEvents(evaluationEvents3);

        // When
        ResponseEntity<ErrorRes> response3 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policyWithoutName),
                ErrorRes.class
        );

        // Then
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response3.getBody()).isNotNull();

        PolicyResource policyWithoutEngine = new PolicyResource();
        policyWithoutEngine.setName("dataproduct-name-checker");
        policyWithoutEngine.setDisplayName("Data Product Name Checker");
        policyWithoutEngine.setBlockingFlag(true);
        policyWithoutEngine.setSuite("Suite Name");

        List<PolicyEvaluationEventResource> evaluationEvents4 = new ArrayList<>();
        PolicyEvaluationEventResource event4 = new PolicyEvaluationEventResource();
        event4.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents4.add(event4);
        policyWithoutEngine.setEvaluationEvents(evaluationEvents4);

        // When
        ResponseEntity<ErrorRes> response4 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policyWithoutEngine),
                ErrorRes.class
        );

        // Then
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response4.getBody()).isNotNull();

        PolicyResource validPolicy = new PolicyResource();
        validPolicy.setName("dataproduct-name-checker");
        validPolicy.setDisplayName("Data Product Name Checker");
        validPolicy.setDescription("Description");
        validPolicy.setBlockingFlag(true);
        validPolicy.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef4 = new PolicyEngineResource();
        policyEngineRef4.setId(engineId);
        policyEngineRef4.setName("opa-policy-checker");
        validPolicy.setPolicyEngine(policyEngineRef4);

        List<PolicyEvaluationEventResource> evaluationEvents5 = new ArrayList<>();
        PolicyEvaluationEventResource event5 = new PolicyEvaluationEventResource();
        event5.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents5.add(event5);
        validPolicy.setEvaluationEvents(evaluationEvents5);

        ResponseEntity<PolicyResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(validPolicy),
                PolicyResource.class
        );
        PolicyResource createdPolicy = createResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();

        PolicyResource duplicatePolicy = new PolicyResource();
        duplicatePolicy.setName("dataproduct-name-checker");
        duplicatePolicy.setDisplayName("Data Product Name Checker 2");
        duplicatePolicy.setDescription("Description 2");
        duplicatePolicy.setBlockingFlag(false);
        duplicatePolicy.setSuite("Suite Name 2");

        PolicyEngineResource policyEngineRef5 = new PolicyEngineResource();
        policyEngineRef5.setId(engineId);
        policyEngineRef5.setName("opa-policy-checker");
        duplicatePolicy.setPolicyEngine(policyEngineRef5);

        List<PolicyEvaluationEventResource> evaluationEvents6 = new ArrayList<>();
        PolicyEvaluationEventResource event6 = new PolicyEvaluationEventResource();
        event6.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents6.add(event6);
        duplicatePolicy.setEvaluationEvents(evaluationEvents6);

        // When
        ResponseEntity<ErrorRes> response5 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(duplicatePolicy),
                ErrorRes.class
        );

        // Then
        assertThat(response5.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response5.getBody()).isNotNull();
        if (response5.getBody() != null) {
            assertThat(response5.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS.code());
        }

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine exists
     * And: A policy resource is created
     * When: A policy update request is made with null policy
     * Then: A 400 BAD_REQUEST error is returned
     * And: The error code is SC400_02_POLICY_IS_EMPTY
     * And: The error message indicates that the policy object cannot be null
     */
    @Test
    public void whenUpdatePolicyWithNullThenReturnBadRequest() {
        // Given
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-name-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Name Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        ResponseEntity<PolicyResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = createResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();

        // When
        ResponseEntity<ErrorRes> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.PUT,
                new HttpEntity<>(null),
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC400_02_POLICY_IS_EMPTY.code());

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine exists
     * And: A policy resource is created
     * And: An updated policy resource is prepared
     * When: A policy update request is made with a non-existent policy ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_02_POLICY_NOT_FOUND
     * And: The error message indicates that the resource with the specified ID was not found
     * When: A policy update request is made with a non-existent policy engine ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_01_POLICY_ENGINE_NOT_FOUND
     * And: The error message indicates that the policy engine with the specified ID was not found
     */
    @Test
    public void whenUpdatePolicyWithNonExistentIdThenReturnNotFound() {
        // Given
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-name-checker-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Name Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        ResponseEntity<PolicyResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = createResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Date createdAt = createdPolicy.getCreatedAt();

        PolicyResource updatedPolicy = new PolicyResource();
        updatedPolicy.setName(uniquePolicyName);
        updatedPolicy.setDisplayName("Data Product Name Checker");
        updatedPolicy.setDescription("Updated description");
        updatedPolicy.setBlockingFlag(false);
        updatedPolicy.setSuite("Suite Name");
        updatedPolicy.setCreatedAt(createdAt);

        PolicyEngineResource updatedPolicyEngineRef = new PolicyEngineResource();
        updatedPolicyEngineRef.setId(engineId);
        updatedPolicyEngineRef.setName(uniqueEngineName);
        updatedPolicy.setPolicyEngine(updatedPolicyEngineRef);

        List<PolicyEvaluationEventResource> updatedEvaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource updatedEvent = new PolicyEvaluationEventResource();
        updatedEvent.setEvent("DATA_PRODUCT_VERSION_CREATION");
        updatedEvaluationEvents.add(updatedEvent);
        updatedPolicy.setEvaluationEvents(updatedEvaluationEvents);

        // When
        ResponseEntity<ErrorRes> response1 = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/99999",
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicy),
                ErrorRes.class
        );

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response1.getBody()).isNotNull();
        assertThat(response1.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND.code());

        PolicyEngineResource nonExistentEngineRef = new PolicyEngineResource();
        nonExistentEngineRef.setId(99999L);
        nonExistentEngineRef.setName("non-existent-engine");
        updatedPolicy.setPolicyEngine(nonExistentEngineRef);

        // When
        ResponseEntity<ErrorRes> response2 = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + createdPolicy.getRootId(),
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicy),
                ErrorRes.class
        );

        // Then
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response2.getBody()).isNotNull();
        assertThat(response2.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND.code());

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + createdPolicy.getRootId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: A policy engine exists
     * And: A policy resource is created
     * And: An updated policy resource is prepared
     * When: A policy update request is made with null name
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that policy name cannot be null
     * When: A policy update request is made with null policyEngine
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error message indicates that policyEngineId or PolicyEngine object cannot be null
     * When: A policy update request is made with a name that already exists with a different rootID
     * Then: A 422 UNPROCESSABLE_ENTITY error is returned
     * And: The error code is SC422_04_POLICY_ALREADY_EXISTS
     * And: The error message indicates that a policy with the same name already exists with a different rootID
     */
    @Test
    public void whenUpdatePolicyWithInvalidDataThenReturnUnprocessableEntity() {
        // Given
        String uniqueEngineName = "opa-policy-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName = "dataproduct-name-checker-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName2 = "dataproduct-name-checker-2-" + UUID.randomUUID().toString().substring(0, 8);
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(uniqueEngineName);
        policyEngine.setDisplayName("OPA Policy Checker");
        policyEngine.setAdapterUrl("http://localhost:9001/api/v1/up/validator");

        ResponseEntity<PolicyEngineResource> engineResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine),
                PolicyEngineResource.class
        );
        assertThat(engineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine = engineResponse.getBody();
        assertThat(createdEngine).isNotNull();
        Long engineId = createdEngine.getId();
        assertThat(engineId).isNotNull();

        PolicyResource policy = new PolicyResource();
        policy.setName(uniquePolicyName);
        policy.setDisplayName("Data Product Name Checker");
        policy.setDescription("Description");
        policy.setBlockingFlag(true);
        policy.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        ResponseEntity<PolicyResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = createResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long rootId = createdPolicy.getRootId();
        assertThat(rootId).isNotNull();
        Date createdAt = createdPolicy.getCreatedAt();

        PolicyResource invalidPolicy1 = new PolicyResource();
        invalidPolicy1.setRootId(rootId);
        invalidPolicy1.setDisplayName("Data Product Name Checker");
        invalidPolicy1.setDescription("Description");
        invalidPolicy1.setBlockingFlag(true);
        invalidPolicy1.setSuite("Suite Name");
        invalidPolicy1.setCreatedAt(createdAt);

        PolicyEngineResource invalidPolicyEngineRef1 = new PolicyEngineResource();
        invalidPolicyEngineRef1.setId(engineId);
        invalidPolicyEngineRef1.setName(uniqueEngineName);
        invalidPolicy1.setPolicyEngine(invalidPolicyEngineRef1);

        List<PolicyEvaluationEventResource> invalidEvaluationEvents1 = new ArrayList<>();
        PolicyEvaluationEventResource invalidEvent1 = new PolicyEvaluationEventResource();
        invalidEvent1.setEvent("DATA_PRODUCT_VERSION_CREATION");
        invalidEvaluationEvents1.add(invalidEvent1);
        invalidPolicy1.setEvaluationEvents(invalidEvaluationEvents1);

        // When
        ResponseEntity<ErrorRes> response1 = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidPolicy1),
                ErrorRes.class
        );

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response1.getBody()).isNotNull();

        PolicyResource invalidPolicy2 = new PolicyResource();
        invalidPolicy2.setRootId(rootId);
        invalidPolicy2.setName(uniquePolicyName);
        invalidPolicy2.setDisplayName("Data Product Name Checker");
        invalidPolicy2.setDescription("Description");
        invalidPolicy2.setBlockingFlag(true);
        invalidPolicy2.setSuite("Suite Name");
        invalidPolicy2.setCreatedAt(createdAt);

        List<PolicyEvaluationEventResource> invalidEvaluationEvents2 = new ArrayList<>();
        PolicyEvaluationEventResource invalidEvent2 = new PolicyEvaluationEventResource();
        invalidEvent2.setEvent("DATA_PRODUCT_VERSION_CREATION");
        invalidEvaluationEvents2.add(invalidEvent2);
        invalidPolicy2.setEvaluationEvents(invalidEvaluationEvents2);

        // When
        ResponseEntity<ErrorRes> response2 = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.PUT,
                new HttpEntity<>(invalidPolicy2),
                ErrorRes.class
        );

        // Then
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response2.getBody()).isNotNull();

        PolicyResource policy2 = new PolicyResource();
        policy2.setName(uniquePolicyName2);
        policy2.setDisplayName("Data Product Name Checker 2");
        policy2.setDescription("Description 2");
        policy2.setBlockingFlag(false);
        policy2.setSuite("Suite Name 2");

        PolicyEngineResource policyEngineRef2 = new PolicyEngineResource();
        policyEngineRef2.setId(engineId);
        policyEngineRef2.setName(uniqueEngineName);
        policy2.setPolicyEngine(policyEngineRef2);

        List<PolicyEvaluationEventResource> evaluationEvents2 = new ArrayList<>();
        PolicyEvaluationEventResource event2 = new PolicyEvaluationEventResource();
        event2.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents2.add(event2);
        policy2.setEvaluationEvents(evaluationEvents2);

        ResponseEntity<PolicyResource> createResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy2),
                PolicyResource.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy2 = createResponse2.getBody();
        assertThat(createdPolicy2).isNotNull();
        Long rootId2 = createdPolicy2.getRootId();
        assertThat(rootId2).isNotNull();
        Date createdAt2 = createdPolicy2.getCreatedAt();

        PolicyResource duplicateNamePolicy = new PolicyResource();
        duplicateNamePolicy.setRootId(rootId2);
        duplicateNamePolicy.setName(uniquePolicyName);
        duplicateNamePolicy.setDisplayName("Data Product Name Checker 2");
        duplicateNamePolicy.setDescription("Description 2");
        duplicateNamePolicy.setBlockingFlag(false);
        duplicateNamePolicy.setSuite("Suite Name 2");
        duplicateNamePolicy.setCreatedAt(createdAt2);

        PolicyEngineResource duplicatePolicyEngineRef = new PolicyEngineResource();
        duplicatePolicyEngineRef.setId(engineId);
        duplicatePolicyEngineRef.setName(uniqueEngineName);
        duplicateNamePolicy.setPolicyEngine(duplicatePolicyEngineRef);

        List<PolicyEvaluationEventResource> duplicateEvaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource duplicateEvent = new PolicyEvaluationEventResource();
        duplicateEvent.setEvent("DATA_PRODUCT_VERSION_CREATION");
        duplicateEvaluationEvents.add(duplicateEvent);
        duplicateNamePolicy.setEvaluationEvents(duplicateEvaluationEvents);

        // When
        ResponseEntity<ErrorRes> response3 = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId2,
                HttpMethod.PUT,
                new HttpEntity<>(duplicateNamePolicy),
                ErrorRes.class
        );

        // Then
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response3.getBody()).isNotNull();
        assertThat(response3.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS.code());

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId2,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.ENGINES) + "/" + engineId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    /**
     * Given: No policy exists with the specified ID
     * When: A policy retrieval request is made with a non-existent ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_02_POLICY_NOT_FOUND
     * And: The error message indicates that the resource with the specified root ID was not found
     */
    @Test
    public void whenReadOnePolicyWithNonExistentIdThenReturnNotFound() {
        // Given
        // No policy exists with the specified ID

        // When
        ResponseEntity<ErrorRes> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/99999",
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        if (response.getBody() != null) {
            assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND.code());
        }
    }

    /**
     * Given: No policy exists with the specified ID
     * When: A policy deletion request is made with a non-existent ID
     * Then: A 404 NOT_FOUND error is returned
     * And: The error code is SC404_02_POLICY_NOT_FOUND
     * And: The error message indicates that the resource with the specified root ID was not found
     */
    @Test
    public void whenDeletePolicyWithNonExistentIdThenReturnNotFound() {
        // Given
        // No policy exists with the specified ID

        // When
        ResponseEntity<ErrorRes> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/99999",
                HttpMethod.DELETE,
                null,
                ErrorRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        if (response.getBody() != null) {
            assertThat(response.getBody().getCode()).isEqualTo(PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND.code());
        }
    }
}
