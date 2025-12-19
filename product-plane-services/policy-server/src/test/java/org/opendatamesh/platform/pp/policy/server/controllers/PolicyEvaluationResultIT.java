package org.opendatamesh.platform.pp.policy.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.resources.*;
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
 * Integration tests for PolicyEvaluationResult resource operations.
 * These tests verify CRUD operations and business logic for PolicyEvaluationResult resources.
 */
public class PolicyEvaluationResultIT extends PolicyApplicationIT {

    /**
     * Given: A policy engine exists
     * And: A policy resource exists
     * And: A policy evaluation result resource is created from a JSON file
     * When: The policy evaluation result is created via API
     * Then: The policy evaluation result is successfully created
     * And: The result has correct ID, policyId, result, dataProductId, dataProductVersion, inputObject, outputObject, and timestamps
     */
    @Test
    public void whenCreatePolicyEvaluationResultThenReturnCreatedPolicyEvaluationResult() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result = new PolicyEvaluationResultResource();
        result.setDataProductId("abc123");
        result.setDataProductVersion("1.0.0");
        result.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}"));
        result.setOutputObject("{\"allow\":true}");
        result.setResult(true);
        result.setPolicyId(policyId);

        // When
        ResponseEntity<PolicyEvaluationResultResource> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result),
                PolicyEvaluationResultResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        PolicyEvaluationResultResource createdResult = response.getBody();
        assertThat(createdResult.getId()).isNotNull();
        assertThat(createdResult.getPolicyId()).isEqualTo(policyId);
        assertThat(createdResult.getResult()).isTrue();
        assertThat(createdResult.getDataProductId()).isEqualTo("abc123");
        assertThat(createdResult.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(createdResult.getInputObject()).isNotNull();
        assertThat(createdResult.getOutputObject()).isEqualTo("{\"allow\":true}");
        assertThat(createdResult.getCreatedAt()).isNotNull();
        assertThat(createdResult.getUpdatedAt()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + createdResult.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
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
     * And: A policy resource exists
     * And: A policy evaluation result resource is created
     * And: An updated policy evaluation result
     * When: The policy evaluation result is updated via PUT request
     * Then: The policy evaluation result is successfully updated
     * And: The updated result has the new result value
     * And: The updatedAt timestamp is after the createdAt timestamp
     */
    @Test
    public void whenUpdatePolicyEvaluationResultThenReturnUpdatedPolicyEvaluationResult() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result = new PolicyEvaluationResultResource();
        result.setDataProductId("abc123");
        result.setDataProductVersion("1.0.0");
        result.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}"));
        result.setOutputObject("{\"allow\":true}");
        result.setResult(true);
        result.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult = createResponse.getBody();
        assertThat(createdResult).isNotNull();
        Long resultId = createdResult.getId();
        assertThat(resultId).isNotNull();
        Date createdAt = createdResult.getCreatedAt();

        PolicyEvaluationResultResource updatedResult = new PolicyEvaluationResultResource();
        updatedResult.setId(resultId);
        updatedResult.setDataProductId("abc123");
        updatedResult.setDataProductVersion("1.0.0");
        updatedResult.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}"));
        updatedResult.setOutputObject("{\"allow\":false}");
        updatedResult.setResult(false);
        updatedResult.setPolicyId(policyId);
        updatedResult.setCreatedAt(createdAt);

        // When
        ResponseEntity<PolicyEvaluationResultResource> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedResult),
                PolicyEvaluationResultResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyEvaluationResultResource updatedResultResponse = response.getBody();
        assertThat(updatedResultResponse.getId()).isEqualTo(resultId);
        assertThat(updatedResultResponse.getResult()).isFalse();
        assertThat(updatedResultResponse.getOutputObject()).isEqualTo("{\"allow\":false}");
        assertThat(updatedResultResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedResultResponse.getUpdatedAt()).isNotNull();
        assertThat(updatedResultResponse.getUpdatedAt()).isNotEqualTo(createdAt);

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
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
     * And: A policy resource exists
     * And: Multiple policy evaluation results are created
     * When: All policy evaluation results are retrieved via GET request
     * Then: All policy evaluation results are returned in the response
     * And: Each result has the expected properties
     */
    @Test
    public void whenReadAllPolicyEvaluationResultsThenReturnAllPolicyEvaluationResults() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result1 = new PolicyEvaluationResultResource();
        result1.setDataProductId("abc123");
        result1.setDataProductVersion("1.0.0");
        result1.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\"}"));
        result1.setOutputObject("{\"allow\":true}");
        result1.setResult(true);
        result1.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result1),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult1 = createResponse1.getBody();
        assertThat(createdResult1).isNotNull();
        Long resultId1 = createdResult1.getId();
        assertThat(resultId1).isNotNull();

        PolicyEvaluationResultResource result2 = new PolicyEvaluationResultResource();
        result2.setDataProductId("def456");
        result2.setDataProductVersion("2.0.0");
        result2.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-2\"}"));
        result2.setOutputObject("{\"allow\":false}");
        result2.setResult(false);
        result2.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result2),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult2 = createResponse2.getBody();
        assertThat(createdResult2).isNotNull();
        Long resultId2 = createdResult2.getId();
        assertThat(resultId2).isNotNull();

        // When
        ResponseEntity<PageUtility<PolicyEvaluationResultShortResource>> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<PolicyEvaluationResultShortResource>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PageUtility<PolicyEvaluationResultShortResource> page = response.getBody();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);
        boolean foundResult1 = page.getContent().stream()
                .anyMatch(r -> r.getId().equals(resultId1) && r.getDataProductId().equals("abc123"));
        boolean foundResult2 = page.getContent().stream()
                .anyMatch(r -> r.getId().equals(resultId2) && r.getDataProductId().equals("def456"));
        assertThat(foundResult1).isTrue();
        assertThat(foundResult2).isTrue();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId1,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId2,
                HttpMethod.DELETE,
                null,
                Void.class
        );
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
     * And: A policy resource exists
     * And: Multiple policy evaluation results are created
     * When: All policy evaluation results are retrieved via GET request
     * Then: Short versions of all policy evaluation results are returned
     * And: Each short result contains only essential fields (ID, result, dataProductId, policy summary, timestamps)
     * And: Full resource fields like inputObject and outputObject are not present
     */
    @Test
    public void whenReadAllPolicyEvaluationResultsShortThenReturnShortVersions() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result = new PolicyEvaluationResultResource();
        result.setDataProductId("abc123");
        result.setDataProductVersion("1.0.0");
        result.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\"}"));
        result.setOutputObject("{\"allow\":true}");
        result.setResult(true);
        result.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult = createResponse.getBody();
        assertThat(createdResult).isNotNull();
        Long resultId = createdResult.getId();
        assertThat(resultId).isNotNull();

        // When
        ResponseEntity<PageUtility<PolicyEvaluationResultShortResource>> response = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<PolicyEvaluationResultShortResource>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PageUtility<PolicyEvaluationResultShortResource> page = response.getBody();
        assertThat(page.getContent()).isNotEmpty();
        PolicyEvaluationResultShortResource shortResult = page.getContent().stream()
                .filter(r -> r.getId().equals(resultId))
                .findFirst()
                .orElse(null);
        assertThat(shortResult).isNotNull();
        assertThat(shortResult.getId()).isEqualTo(resultId);
        assertThat(shortResult.getResult()).isTrue();
        assertThat(shortResult.getDataProductId()).isEqualTo("abc123");
        assertThat(shortResult.getPolicy()).isNotNull();
        assertThat(shortResult.getCreatedAt()).isNotNull();
        assertThat(shortResult.getUpdatedAt()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
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
     * And: A policy resource exists
     * And: Multiple policy evaluation results are created with different dataProductIds
     * When: Policy evaluation results are retrieved with default search options
     * Then: All results within the default time window are returned
     * When: Policy evaluation results are retrieved filtered by dataProductId
     * Then: Only results matching the specified dataProductId are returned
     */
    @Test
    public void whenReadAllPolicyEvaluationResultsWithSearchOptionsThenReturnFilteredResults() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result1 = new PolicyEvaluationResultResource();
        result1.setDataProductId("abc123");
        result1.setDataProductVersion("1.0.0");
        result1.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\"}"));
        result1.setOutputObject("{\"allow\":true}");
        result1.setResult(true);
        result1.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result1),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult1 = createResponse1.getBody();
        assertThat(createdResult1).isNotNull();
        Long resultId1 = createdResult1.getId();
        assertThat(resultId1).isNotNull();

        PolicyEvaluationResultResource result2 = new PolicyEvaluationResultResource();
        result2.setDataProductId("def456");
        result2.setDataProductVersion("2.0.0");
        result2.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-2\"}"));
        result2.setOutputObject("{\"allow\":false}");
        result2.setResult(false);
        result2.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result2),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult2 = createResponse2.getBody();
        assertThat(createdResult2).isNotNull();
        Long resultId2 = createdResult2.getId();
        assertThat(resultId2).isNotNull();

        // When
        ResponseEntity<PageUtility<PolicyEvaluationResultShortResource>> response1 = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<PolicyEvaluationResultShortResource>>() {
                }
        );

        // Then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response1.getBody()).isNotNull();
        PageUtility<PolicyEvaluationResultShortResource> page1 = response1.getBody();
        assertThat(page1.getContent().size()).isGreaterThanOrEqualTo(2);

        // When
        ResponseEntity<PageUtility<PolicyEvaluationResultShortResource>> response2 = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "?dataProductId=abc123",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<PolicyEvaluationResultShortResource>>() {
                }
        );

        // Then
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isNotNull();
        PageUtility<PolicyEvaluationResultShortResource> page2 = response2.getBody();
        boolean foundResult1 = page2.getContent().stream()
                .anyMatch(r -> r.getId().equals(resultId1) && r.getDataProductId().equals("abc123"));
        boolean foundResult2 = page2.getContent().stream()
                .anyMatch(r -> r.getId().equals(resultId2));
        assertThat(foundResult1).isTrue();
        assertThat(foundResult2).isFalse();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId1,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId2,
                HttpMethod.DELETE,
                null,
                Void.class
        );
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
     * And: A policy resource exists
     * And: A policy evaluation result resource is created
     * When: The policy evaluation result is retrieved by ID via GET request
     * Then: The policy evaluation result is successfully retrieved
     * And: The result has all expected fields including full inputObject and outputObject
     */
    @Test
    public void whenReadOnePolicyEvaluationResultThenReturnPolicyEvaluationResult() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result = new PolicyEvaluationResultResource();
        result.setDataProductId("abc123");
        result.setDataProductVersion("1.0.0");
        result.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}"));
        result.setOutputObject("{\"allow\":true}");
        result.setResult(true);
        result.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult = createResponse.getBody();
        assertThat(createdResult).isNotNull();
        Long resultId = createdResult.getId();
        assertThat(resultId).isNotNull();

        // When
        ResponseEntity<PolicyEvaluationResultResource> response = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                PolicyEvaluationResultResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        PolicyEvaluationResultResource retrievedResult = response.getBody();
        assertThat(retrievedResult.getId()).isEqualTo(resultId);
        assertThat(retrievedResult.getPolicyId()).isEqualTo(policyId);
        assertThat(retrievedResult.getResult()).isTrue();
        assertThat(retrievedResult.getDataProductId()).isEqualTo("abc123");
        assertThat(retrievedResult.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(retrievedResult.getInputObject()).isNotNull();
        assertThat(retrievedResult.getOutputObject()).isEqualTo("{\"allow\":true}");
        assertThat(retrievedResult.getCreatedAt()).isNotNull();
        assertThat(retrievedResult.getUpdatedAt()).isNotNull();

        // Cleanup
        rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
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
     * And: A policy resource exists
     * And: A policy evaluation result resource is created
     * When: The policy evaluation result is deleted via DELETE request
     * Then: The policy evaluation result is successfully deleted
     * And: Attempting to retrieve the policy evaluation result returns a 404 NOT_FOUND error
     */
    @Test
    public void whenDeletePolicyEvaluationResultThenPolicyEvaluationResultIsDeleted() throws JsonProcessingException {
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

        ResponseEntity<PolicyResource> policyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy),
                PolicyResource.class
        );
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy = policyResponse.getBody();
        assertThat(createdPolicy).isNotNull();
        Long policyId = createdPolicy.getId();
        assertThat(policyId).isNotNull();

        PolicyEvaluationResultResource result = new PolicyEvaluationResultResource();
        result.setDataProductId("abc123");
        result.setDataProductVersion("1.0.0");
        result.setInputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"name\":\"dp-1\"}"));
        result.setOutputObject("{\"allow\":true}");
        result.setResult(true);
        result.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> createResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                new HttpEntity<>(result),
                PolicyEvaluationResultResource.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyEvaluationResultResource createdResult = createResponse.getBody();
        assertThat(createdResult).isNotNull();
        Long resultId = createdResult.getId();
        assertThat(resultId).isNotNull();

        // When
        ResponseEntity<Void> deleteResponse = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<PolicyEvaluationResultResource> getResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS) + "/" + resultId,
                PolicyEvaluationResultResource.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

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
}
