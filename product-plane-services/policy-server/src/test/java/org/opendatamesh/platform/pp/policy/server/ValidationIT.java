package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.opendatamesh.platform.pp.policy.server.services.proxies.ValidatorProxy;
import org.opendatamesh.platform.up.validator.api.resources.EvaluationResource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationIT extends ODMPolicyIT {

    @MockBean
    protected ValidatorProxy validatorProxy;

    // ======================================================================================
    // VALIDATE Object
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testValidateObjectSpELFilteringPassed() {
        // Mock Policy Engine interactions
        EvaluationResource mockResponse = new EvaluationResource();
        mockResponse.setPolicyEvaluationId(1L); // This value is not checked in assertions
        mockResponse.setEvaluationResult(true);
        mockResponse.setOutputObject("{\"message\": \"OK\"}");
        Mockito.when(validatorProxy.validatePolicy(Mockito.any(), Mockito.any(), false))
                .thenReturn(mockResponse);

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_2);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_2, parentEngineResource.getId());
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_3, parentEngineResource.getId());
        PolicyEvaluationRequestResource evaluationRequestResource = createPolicyEvaluationRequestResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_REQUEST
        );
        ResponseEntity<ObjectNode> postResponse =
                policyClient.validateInputObjectResponseEntity(evaluationRequestResource);
        verifyResponseEntity(postResponse, HttpStatus.OK, true);
        ValidationResponseResource validationResponseResource = mapper.convertValue(
                postResponse.getBody(), ValidationResponseResource.class
        );
        List<PolicyEvaluationResultResource> evaluatedPolicies = validationResponseResource.getPolicyResults();

        // Verification
        verifyResponseEntity(postResponse, HttpStatus.OK,true);
        assertThat(validationResponseResource).isNotNull();
        assertThat(evaluatedPolicies.size()).isEqualTo(3);

        // Verify single policies (verify that they match the event type)
        PolicyResource policyResource = policyClient.getPolicyVersion(evaluatedPolicies.get(0).getPolicyId());
        assertThat(policyResource.getEvaluationEvents().stream().map(PolicyEvaluationEventResource::getEvent).collect(Collectors.toSet())).contains(evaluationRequestResource.getEvent().toString());
        policyResource = policyClient.getPolicyVersion(evaluatedPolicies.get(1).getPolicyId());
        assertThat(policyResource.getEvaluationEvents().stream().map(PolicyEvaluationEventResource::getEvent).collect(Collectors.toSet())).contains(evaluationRequestResource.getEvent().toString());

        // Verify PolicyEvaluationResults in DB
        ResponseEntity<ObjectNode> getResponse = policyClient.readAllPolicyEvaluationResultsResponseEntity();
        List<PolicyEvaluationResultShortResource> policyEvaluationResults = extractListFromPageFromObjectNode(
                getResponse.getBody(), PolicyEvaluationResultShortResource.class
        );
        assertThat(policyEvaluationResults.size()).isEqualTo(3);
        // Verify that the short resources contain the expected data
        assertThat(policyEvaluationResults.get(0).getPolicy().getId()).isEqualTo(evaluatedPolicies.get(0).getPolicyId());
        assertThat(policyEvaluationResults.get(0).getResult()).isEqualTo(evaluatedPolicies.get(0).getResult());
        assertThat(policyEvaluationResults.get(0).getDataProductId()).isEqualTo(evaluatedPolicies.get(0).getDataProductId());
        assertThat(policyEvaluationResults.get(1).getPolicy().getId()).isEqualTo(evaluatedPolicies.get(1).getPolicyId());
        assertThat(policyEvaluationResults.get(1).getResult()).isEqualTo(evaluatedPolicies.get(1).getResult());
        assertThat(policyEvaluationResults.get(1).getDataProductId()).isEqualTo(evaluatedPolicies.get(1).getDataProductId());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testValidateObjectSpELFilteringNotPassed() {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_2);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_2, parentEngineResource.getId());
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_3, parentEngineResource.getId());
        PolicyEvaluationRequestResource evaluationRequestResource = createPolicyEvaluationRequestResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_REQUEST_2
        );
        ResponseEntity<ObjectNode> postResponse =
                policyClient.validateInputObjectResponseEntity(evaluationRequestResource);
        verifyResponseEntity(postResponse, HttpStatus.OK, true);
        ValidationResponseResource validationResponseResource = mapper.convertValue(
                postResponse.getBody(), ValidationResponseResource.class
        );
        List<PolicyEvaluationResultResource> evaluatedPolicies = validationResponseResource.getPolicyResults();

        // Verification
        verifyResponseEntity(postResponse, HttpStatus.OK,true);
        assertThat(validationResponseResource).isNotNull();
        // Assert that no policies are validated thanks to PolicySelector filtering with SpEL expression
        assertThat(evaluatedPolicies.size()).isEqualTo(0);

    }

}
