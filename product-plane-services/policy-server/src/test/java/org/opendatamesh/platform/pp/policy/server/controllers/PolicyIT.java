package org.opendatamesh.platform.pp.policy.server.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationEventResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.client.utils.jackson.PageUtility;
import org.springframework.core.ParameterizedTypeReference;
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
 * Integration tests for Policy resource operations.
 * These tests verify CRUD operations and business logic for Policy resources.
 */
public class PolicyIT extends PolicyApplicationIT {

    /**
     * Given: A policy engine exists
     * And: A policy resource is created from a JSON file
     * When: The policy is created via API
     * Then: The policy is successfully created with all expected fields
     * And: The policy has correct ID, rootId, name, displayName, description, blockingFlag, rawContent, evaluationEvents, suite, and timestamps
     */
    @Test
    public void whenCreatePolicyThenReturnCreatedPolicy() {
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
        policy.setDescription("Check whether the name of the input Data Product is compliant with global naming convention or not");
        policy.setBlockingFlag(true);
        policy.setRawContent("package " + uniquePolicyName + "\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        policy.setSuite("Suite Name");
        policy.setFilteringExpression("afterState.dataProductVersion.info.domain == 'sampleDomain' && afterState.dataProductVersion.info.domain != 'sampleDomainTwo'");

        PolicyEngineResource policyEngineRef = new PolicyEngineResource();
        policyEngineRef.setId(engineId);
        policyEngineRef.setName(uniqueEngineName);
        policy.setPolicyEngine(policyEngineRef);

        List<PolicyEvaluationEventResource> evaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource event = new PolicyEvaluationEventResource();
        event.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents.add(event);
        policy.setEvaluationEvents(evaluationEvents);

        // When
        ResponseEntity<PolicyResource> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        PolicyResource createdPolicy = response.getBody();
        assertThat(createdPolicy.getId()).isNotNull();
        assertThat(createdPolicy.getRootId()).isNotNull();
        assertThat(createdPolicy.getName()).isEqualTo(uniquePolicyName);
        assertThat(createdPolicy.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(createdPolicy.getDescription()).isEqualTo("Check whether the name of the input Data Product is compliant with global naming convention or not");
        assertThat(createdPolicy.getBlockingFlag()).isTrue();
        assertThat(createdPolicy.getRawContent()).isEqualTo("package " + uniquePolicyName + "\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(createdPolicy.getSuite()).isEqualTo("Suite Name");
        assertThat(createdPolicy.getEvaluationEvents()).isNotNull();
        assertThat(createdPolicy.getEvaluationEvents().size()).isEqualTo(1);
        assertThat(createdPolicy.getEvaluationEvents().get(0).getEvent()).isEqualTo("DATA_PRODUCT_VERSION_CREATION");
        assertThat(createdPolicy.getCreatedAt()).isNotNull();
        assertThat(createdPolicy.getUpdatedAt()).isNotNull();

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
     * When: The policy is updated via PUT request
     * Then: The policy is successfully updated
     * And: A new version of the policy is created with incremented ID
     * And: The old version is still accessible via version ID
     * And: The updated policy has the new values for description and blockingFlag
     */
    @Test
    public void whenUpdatePolicyThenReturnUpdatedPolicy() {
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
        policy.setDescription("Check whether the name of the input Data Product is compliant with global naming convention or not");
        policy.setBlockingFlag(true);
        policy.setRawContent("package " + uniquePolicyName + "\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
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
        Long originalVersionId = createdPolicy.getId();
        assertThat(originalVersionId).isNotNull();
        Date createdAt = createdPolicy.getCreatedAt();

        PolicyResource updatedPolicy = new PolicyResource();
        updatedPolicy.setRootId(rootId);
        updatedPolicy.setName(uniquePolicyName);
        updatedPolicy.setDisplayName("Data Product Name Checker");
        updatedPolicy.setDescription("Check the Data Product name");
        updatedPolicy.setBlockingFlag(false);
        updatedPolicy.setRawContent("package " + uniquePolicyName + "\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
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
        ResponseEntity<PolicyResource> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicy),
                PolicyResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyResource updatedPolicyResponse = response.getBody();
        assertThat(updatedPolicyResponse.getId()).isNotEqualTo(originalVersionId);
        assertThat(updatedPolicyResponse.getRootId()).isEqualTo(rootId);
        assertThat(updatedPolicyResponse.getDescription()).isEqualTo("Check the Data Product name");
        assertThat(updatedPolicyResponse.getBlockingFlag()).isFalse();

        ResponseEntity<PolicyResource> oldVersionResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/versions/" + originalVersionId,
                PolicyResource.class
        );
        assertThat(oldVersionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(oldVersionResponse.getBody()).isNotNull();
        PolicyResource oldVersion = oldVersionResponse.getBody();
        assertThat(oldVersion.getId()).isEqualTo(originalVersionId);
        assertThat(oldVersion.getDescription()).isEqualTo("Check whether the name of the input Data Product is compliant with global naming convention or not");
        assertThat(oldVersion.getBlockingFlag()).isTrue();

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
     * Given: Multiple policy engines exist
     * And: Multiple policies are created with different engines
     * When: All policies are retrieved via GET request
     * Then: All policies are returned in the response
     * And: Each policy has the expected properties
     */
    @Test
    public void whenReadAllPoliciesThenReturnAllPolicies() {
        // Given
        String uniqueEngineName1 = "opa-policy-checker-1-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueEngineName2 = "opa-policy-checker-2-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName1 = "dataproduct-name-checker-1-" + UUID.randomUUID().toString().substring(0, 8);
        String uniquePolicyName2 = "dataproduct-name-checker-2-" + UUID.randomUUID().toString().substring(0, 8);

        PolicyEngineResource policyEngine1 = new PolicyEngineResource();
        policyEngine1.setName(uniqueEngineName1);
        policyEngine1.setDisplayName("OPA Policy Checker 1");
        policyEngine1.setAdapterUrl("http://localhost:9001/api/v1/up/validator-1");

        ResponseEntity<PolicyEngineResource> engineResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine1),
                PolicyEngineResource.class
        );
        assertThat(engineResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine1 = engineResponse1.getBody();
        assertThat(createdEngine1).isNotNull();
        Long engineId1 = createdEngine1.getId();
        assertThat(engineId1).isNotNull();

        PolicyEngineResource policyEngine2 = new PolicyEngineResource();
        policyEngine2.setName(uniqueEngineName2);
        policyEngine2.setDisplayName("OPA Policy Checker 2");
        policyEngine2.setAdapterUrl("http://localhost:9001/api/v1/up/validator-2");

        ResponseEntity<PolicyEngineResource> engineResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                new HttpEntity<>(policyEngine2),
                PolicyEngineResource.class
        );
        assertThat(engineResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEngineResource createdEngine2 = engineResponse2.getBody();
        assertThat(createdEngine2).isNotNull();
        Long engineId2 = createdEngine2.getId();
        assertThat(engineId2).isNotNull();

        PolicyResource policy1 = new PolicyResource();
        policy1.setName(uniquePolicyName1);
        policy1.setDisplayName("Data Product Name Checker 1");
        policy1.setDescription("Description 1");
        policy1.setBlockingFlag(true);
        policy1.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef1 = new PolicyEngineResource();
        policyEngineRef1.setId(engineId1);
        policyEngineRef1.setName(uniqueEngineName1);
        policy1.setPolicyEngine(policyEngineRef1);

        List<PolicyEvaluationEventResource> evaluationEvents1 = new ArrayList<>();
        PolicyEvaluationEventResource event1 = new PolicyEvaluationEventResource();
        event1.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents1.add(event1);
        policy1.setEvaluationEvents(evaluationEvents1);

        ResponseEntity<PolicyResource> createResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy1),
                PolicyResource.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy1 = createResponse1.getBody();
        assertThat(createdPolicy1).isNotNull();
        Long rootId1 = createdPolicy1.getRootId();
        assertThat(rootId1).isNotNull();

        PolicyResource policy2 = new PolicyResource();
        policy2.setName(uniquePolicyName2);
        policy2.setDisplayName("Data Product Name Checker 2");
        policy2.setDescription("Description 2");
        policy2.setBlockingFlag(false);
        policy2.setSuite("Suite Name");

        PolicyEngineResource policyEngineRef2 = new PolicyEngineResource();
        policyEngineRef2.setId(engineId2);
        policyEngineRef2.setName(uniqueEngineName2);
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

        // When
        ResponseEntity<PageUtility<PolicyResource>> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<PolicyResource>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PageUtility<PolicyResource> page = response.getBody();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);
        boolean foundPolicy1 = page.getContent().stream()
                .anyMatch(p -> p.getRootId().equals(rootId1) && p.getName().equals(uniquePolicyName1));
        boolean foundPolicy2 = page.getContent().stream()
                .anyMatch(p -> p.getRootId().equals(rootId2) && p.getName().equals(uniquePolicyName2));
        assertThat(foundPolicy1).isTrue();
        assertThat(foundPolicy2).isTrue();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId1,
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
     * Given: A policy engine exists
     * And: A policy resource is created
     * When: The policy is retrieved by root ID via GET request
     * Then: The policy is successfully retrieved
     * And: The policy has all expected fields and properties
     */
    @Test
    public void whenReadOnePolicyThenReturnPolicy() {
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
        policy.setDescription("Check whether the name of the input Data Product is compliant with global naming convention or not");
        policy.setBlockingFlag(true);
        policy.setRawContent("package " + uniquePolicyName + "\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
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
        ResponseEntity<PolicyResource> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                PolicyResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyResource retrievedPolicy = response.getBody();
        assertThat(retrievedPolicy.getId()).isNotNull();
        assertThat(retrievedPolicy.getRootId()).isEqualTo(rootId);
        assertThat(retrievedPolicy.getName()).isEqualTo(uniquePolicyName);
        assertThat(retrievedPolicy.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(retrievedPolicy.getDescription()).isEqualTo("Check whether the name of the input Data Product is compliant with global naming convention or not");
        assertThat(retrievedPolicy.getBlockingFlag()).isTrue();
        assertThat(retrievedPolicy.getRawContent()).isEqualTo("package " + uniquePolicyName + "\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(retrievedPolicy.getSuite()).isEqualTo("Suite Name");
        assertThat(retrievedPolicy.getCreatedAt()).isNotNull();
        assertThat(retrievedPolicy.getUpdatedAt()).isNotNull();

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
     * And: The policy is updated creating a new version
     * When: The policy is retrieved by root ID
     * Then: The latest version of the policy is returned
     * And: The returned policy has the updated values
     */
    @Test
    public void whenReadOnePolicyByRootIdWhenMultipleVersionsExistsThenReturnLatestVersion() {
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
        policy.setDescription("Original description");
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

        PolicyResource updatedPolicy = new PolicyResource();
        updatedPolicy.setRootId(rootId);
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

        ResponseEntity<PolicyResource> updateResponse = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicy),
                PolicyResource.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        PolicyResource updatedPolicyResponse = updateResponse.getBody();
        assertThat(updatedPolicyResponse).isNotNull();

        // When
        ResponseEntity<PolicyResource> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                PolicyResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyResource retrievedPolicy = response.getBody();
        assertThat(retrievedPolicy.getRootId()).isEqualTo(rootId);
        assertThat(retrievedPolicy.getDescription()).isEqualTo("Updated description");
        assertThat(retrievedPolicy.getBlockingFlag()).isFalse();

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
     * And: The policy is updated creating a new version
     * When: The policy is retrieved by the original version ID
     * Then: The original version of the policy is returned
     * And: The returned policy has the original values
     * And: The lastVersion flag is false
     */
    @Test
    public void whenReadOnePolicyByVersionIdWhenMultipleVersionsExistsThenReturnOriginalVersion() {
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
        policy.setDescription("Original description");
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
        Long originalVersionId = createdPolicy.getId();
        assertThat(originalVersionId).isNotNull();
        Date createdAt = createdPolicy.getCreatedAt();

        PolicyResource updatedPolicy = new PolicyResource();
        updatedPolicy.setRootId(rootId);
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

        ResponseEntity<PolicyResource> updateResponse = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedPolicy),
                PolicyResource.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();

        // When
        ResponseEntity<PolicyResource> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/versions/" + originalVersionId,
                PolicyResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyResource retrievedPolicy = response.getBody();
        assertThat(retrievedPolicy.getId()).isEqualTo(originalVersionId);
        assertThat(retrievedPolicy.getRootId()).isEqualTo(rootId);
        assertThat(retrievedPolicy.getDescription()).isEqualTo("Original description");
        assertThat(retrievedPolicy.getBlockingFlag()).isTrue();
        if (retrievedPolicy.getLastVersion() != null) {
            assertThat(retrievedPolicy.getLastVersion()).isFalse();
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
     * When: The policy is deleted via DELETE request
     * Then: The policy is successfully deleted
     * And: Attempting to retrieve the policy returns a 404 NOT_FOUND error
     */
    @Test
    public void whenDeletePolicyThenPolicyIsDeleted() {
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
        ResponseEntity<Void> deleteResponse = rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<PolicyResource> getResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                PolicyResource.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Cleanup
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
     * When: The policy is deleted
     * And: A new policy with the same name is created
     * Then: The new policy is successfully created
     * And: The new policy can be retrieved successfully
     */
    @Test
    public void whenDeleteAndRecreatePolicyThenNewPolicyIsCreated() {
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
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + rootId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        PolicyResource newPolicy = new PolicyResource();
        newPolicy.setName(uniquePolicyName);
        newPolicy.setDisplayName("Data Product Name Checker");
        newPolicy.setDescription("New description");
        newPolicy.setBlockingFlag(false);
        newPolicy.setSuite("Suite Name");

        PolicyEngineResource newPolicyEngineRef = new PolicyEngineResource();
        newPolicyEngineRef.setId(engineId);
        newPolicyEngineRef.setName(uniqueEngineName);
        newPolicy.setPolicyEngine(newPolicyEngineRef);

        List<PolicyEvaluationEventResource> newEvaluationEvents = new ArrayList<>();
        PolicyEvaluationEventResource newEvent = new PolicyEvaluationEventResource();
        newEvent.setEvent("DATA_PRODUCT_VERSION_CREATION");
        newEvaluationEvents.add(newEvent);
        newPolicy.setEvaluationEvents(newEvaluationEvents);

        ResponseEntity<PolicyResource> recreateResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(newPolicy),
                PolicyResource.class
        );

        // Then
        assertThat(recreateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(recreateResponse.getBody()).isNotNull();
        PolicyResource recreatedPolicy = recreateResponse.getBody();
        assertThat(recreatedPolicy.getName()).isEqualTo(uniquePolicyName);
        assertThat(recreatedPolicy.getDescription()).isEqualTo("New description");

        ResponseEntity<PolicyResource> getResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + recreatedPolicy.getRootId(),
                PolicyResource.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/" + recreatedPolicy.getRootId(),
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
}
