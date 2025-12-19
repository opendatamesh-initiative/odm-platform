package org.opendatamesh.platform.pp.policy.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyAPIRoutes;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.opendatamesh.platform.pp.policy.server.client.utils.jackson.PageUtility;
import org.opendatamesh.platform.pp.policy.server.services.proxies.ValidatorProxy;
import org.opendatamesh.platform.up.validator.api.resources.EvaluationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * Integration tests for Policy validation operations.
 * These tests verify the validation functionality for policy evaluation requests.
 */
public class ValidationIT extends PolicyApplicationIT {

    @Autowired
    private ValidatorProxy validatorProxy;

    /**
     * Given: Multiple policy engines exist
     * And: Multiple policies are created with different engines
     * And: The policies have evaluation events matching the request event type
     * And: A policy evaluation request
     * And: The validator proxy is mocked to return successful evaluation results
     * When: A validation request is made via API
     * Then: The validation is successful
     * And: All matching policies are evaluated
     * And: Policy evaluation results are created in the database
     * And: The validation response contains results for all evaluated policies
     * And: Each policy result matches the corresponding policy's evaluation event
     * And: The short resources in the database contain the expected data matching the evaluated policies
     */
    @Test
    public void whenValidateObjectWithSpELFilteringPassedThenReturnValidationResults() throws JsonProcessingException {
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
        policy1.setFilteringExpression("afterState.dataProductVersion.info.domain == 'sampleDomain' && afterState.dataProductVersion.info.domain != 'sampleDomainTwo'");

        PolicyEngineResource policyEngineRef1 = new PolicyEngineResource();
        policyEngineRef1.setId(engineId1);
        policyEngineRef1.setName(uniqueEngineName1);
        policy1.setPolicyEngine(policyEngineRef1);

        List<PolicyEvaluationEventResource> evaluationEvents1 = new ArrayList<>();
        PolicyEvaluationEventResource event1 = new PolicyEvaluationEventResource();
        event1.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents1.add(event1);
        policy1.setEvaluationEvents(evaluationEvents1);

        ResponseEntity<PolicyResource> policyResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy1),
                PolicyResource.class
        );
        assertThat(policyResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy1 = policyResponse1.getBody();
        assertThat(createdPolicy1).isNotNull();
        Long rootId1 = createdPolicy1.getRootId();
        assertThat(rootId1).isNotNull();

        PolicyResource policy2 = new PolicyResource();
        policy2.setName(uniquePolicyName2);
        policy2.setDisplayName("Data Product Name Checker 2");
        policy2.setDescription("Description 2");
        policy2.setBlockingFlag(false);
        policy2.setSuite("Suite Name");
        policy2.setFilteringExpression("afterState.dataProductVersion.info.domain == 'sampleDomain' && afterState.dataProductVersion.info.domain != 'sampleDomainTwo'");

        PolicyEngineResource policyEngineRef2 = new PolicyEngineResource();
        policyEngineRef2.setId(engineId2);
        policyEngineRef2.setName(uniqueEngineName2);
        policy2.setPolicyEngine(policyEngineRef2);

        List<PolicyEvaluationEventResource> evaluationEvents2 = new ArrayList<>();
        PolicyEvaluationEventResource event2 = new PolicyEvaluationEventResource();
        event2.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents2.add(event2);
        policy2.setEvaluationEvents(evaluationEvents2);

        ResponseEntity<PolicyResource> policyResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy2),
                PolicyResource.class
        );
        assertThat(policyResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy2 = policyResponse2.getBody();
        assertThat(createdPolicy2).isNotNull();
        Long rootId2 = createdPolicy2.getRootId();
        assertThat(rootId2).isNotNull();

        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_VERSION_CREATION);
        evaluationRequest.setCurrentState(ObjectMapperFactory.JSON_MAPPER.readTree("{\"dataProductVersion\":{}}"));
        evaluationRequest.setAfterState(ObjectMapperFactory.JSON_MAPPER.readTree("{\"dataProductVersion\":{\"dataProductDescriptor\":\"1.0.0\",\"info\":{\"fullyQualifiedName\":\"urn:org.opendatamesh:dataproducts:tripExecution\",\"name\":\"tripExecution\",\"version\":\"1.0.0\",\"description\":\"Thisisprod-1\",\"domain\":\"sampleDomain\",\"owner\":{\"id\":\"john.doe@company-xyz.com\"}},\"interfaceComponents\":{\"outputPorts\":[]}}}"));

        EvaluationResource evaluationResponse1 = new EvaluationResource();
        evaluationResponse1.setEvaluationResult(true);
        evaluationResponse1.setOutputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"allow\":true}"));

        EvaluationResource evaluationResponse2 = new EvaluationResource();
        evaluationResponse2.setEvaluationResult(true);
        evaluationResponse2.setOutputObject(ObjectMapperFactory.JSON_MAPPER.readTree("{\"allow\":true}"));

        Mockito.when(validatorProxy.validatePolicy(any(PolicyResource.class), any(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(evaluationResponse1)
                .thenReturn(evaluationResponse2);

        // When
        ResponseEntity<ValidationResponseResource> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.VALIDATION),
                new HttpEntity<>(evaluationRequest),
                ValidationResponseResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        ValidationResponseResource validationResponse = response.getBody();
        assertThat(validationResponse.getResult()).isTrue();
        assertThat(validationResponse.getPolicyResults()).isNotNull();
        assertThat(validationResponse.getPolicyResults().size()).isEqualTo(2);
        assertThat(validationResponse.getPolicyResults().get(0).getPolicyId()).isNotNull();
        assertThat(validationResponse.getPolicyResults().get(1).getPolicyId()).isNotNull();

        ResponseEntity<PageUtility<PolicyEvaluationResultShortResource>> resultsResponse = rest.exchange(
                apiUrl(PolicyAPIRoutes.RESULTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(resultsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resultsResponse.getBody()).isNotNull();
        PageUtility<PolicyEvaluationResultShortResource> page = resultsResponse.getBody();
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);

        // Cleanup
        for (PolicyEvaluationResultShortResource result : page.getContent()) {
            rest.exchange(
                    apiUrl(PolicyAPIRoutes.RESULTS) + "/" + result.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
        }
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
     * Given: Multiple policy engines exist
     * And: Multiple policies are created with different engines
     * And: A policy evaluation request with different event type
     * When: A validation request is made via API
     * Then: The validation is successful
     * And: No policies are evaluated due to PolicySelector filtering with SpEL expression
     * And: The validation response contains an empty list of policy results
     */
    @Test
    public void whenValidateObjectWithSpELFilteringNotPassedThenReturnEmptyResults() throws JsonProcessingException {
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
        policy1.setFilteringExpression("afterState.dataProductVersion.info.domain == 'sampleDomain' && afterState.dataProductVersion.info.domain != 'sampleDomainTwo'");

        PolicyEngineResource policyEngineRef1 = new PolicyEngineResource();
        policyEngineRef1.setId(engineId1);
        policyEngineRef1.setName(uniqueEngineName1);
        policy1.setPolicyEngine(policyEngineRef1);

        List<PolicyEvaluationEventResource> evaluationEvents1 = new ArrayList<>();
        PolicyEvaluationEventResource event1 = new PolicyEvaluationEventResource();
        event1.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents1.add(event1);
        policy1.setEvaluationEvents(evaluationEvents1);

        ResponseEntity<PolicyResource> policyResponse1 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy1),
                PolicyResource.class
        );
        assertThat(policyResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy1 = policyResponse1.getBody();
        assertThat(createdPolicy1).isNotNull();
        Long rootId1 = createdPolicy1.getRootId();
        assertThat(rootId1).isNotNull();

        PolicyResource policy2 = new PolicyResource();
        policy2.setName(uniquePolicyName2);
        policy2.setDisplayName("Data Product Name Checker 2");
        policy2.setDescription("Description 2");
        policy2.setBlockingFlag(false);
        policy2.setSuite("Suite Name");
        policy2.setFilteringExpression("afterState.dataProductVersion.info.domain == 'sampleDomain' && afterState.dataProductVersion.info.domain != 'sampleDomainTwo'");

        PolicyEngineResource policyEngineRef2 = new PolicyEngineResource();
        policyEngineRef2.setId(engineId2);
        policyEngineRef2.setName(uniqueEngineName2);
        policy2.setPolicyEngine(policyEngineRef2);

        List<PolicyEvaluationEventResource> evaluationEvents2 = new ArrayList<>();
        PolicyEvaluationEventResource event2 = new PolicyEvaluationEventResource();
        event2.setEvent("DATA_PRODUCT_VERSION_CREATION");
        evaluationEvents2.add(event2);
        policy2.setEvaluationEvents(evaluationEvents2);

        ResponseEntity<PolicyResource> policyResponse2 = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                new HttpEntity<>(policy2),
                PolicyResource.class
        );
        assertThat(policyResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PolicyResource createdPolicy2 = policyResponse2.getBody();
        assertThat(createdPolicy2).isNotNull();
        Long rootId2 = createdPolicy2.getRootId();
        assertThat(rootId2).isNotNull();

        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_VERSION_CREATION);
        evaluationRequest.setCurrentState(ObjectMapperFactory.JSON_MAPPER.readTree("{\"dataProductVersion\":{}}"));
        evaluationRequest.setAfterState(ObjectMapperFactory.JSON_MAPPER.readTree("{\"dataProductVersion\":{\"dataProductDescriptor\":\"1.0.0\",\"info\":{\"fullyQualifiedName\":\"urn:org.opendatamesh:dataproducts:tripExecution\",\"name\":\"tripExecution\",\"version\":\"1.0.0\",\"description\":\"Thisisprod-1\",\"domain\":\"differentDomain\",\"owner\":{\"id\":\"john.doe@company-xyz.com\"}},\"interfaceComponents\":{\"outputPorts\":[]}}}"));

        // When
        ResponseEntity<ValidationResponseResource> response = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.VALIDATION),
                new HttpEntity<>(evaluationRequest),
                ValidationResponseResource.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        ValidationResponseResource validationResponse = response.getBody();
        assertThat(validationResponse.getResult()).isTrue();
        assertThat(validationResponse.getPolicyResults()).isNotNull();
        assertThat(validationResponse.getPolicyResults().isEmpty()).isTrue();

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
}
