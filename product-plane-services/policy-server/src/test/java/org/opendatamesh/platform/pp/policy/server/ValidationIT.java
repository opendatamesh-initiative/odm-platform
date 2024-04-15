package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationIT extends ODMPolicyIT {

    // ======================================================================================
    // VALIDATE Object
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testValidateObjectSpELFilteringPassed() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_2);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_2, parentEngineResource.getId());
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_3, parentEngineResource.getId());
        PolicyEvaluationRequestResource evaluationRequestResource = createPolicyEvaluationRequestResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_REQUEST
        );
        ResponseEntity<ValidationResponseResource> postResponse =
                policyClient.validateInputObjectResponseEntity(evaluationRequestResource);
        verifyResponseEntity(postResponse, HttpStatus.OK, true);
        ValidationResponseResource validationResponseResource = postResponse.getBody();
        List<PolicyEvaluationResultResource> evaluatedPolicies = validationResponseResource.getPolicyResults();

        // Verification
        verifyResponseEntity(postResponse, HttpStatus.OK,true);
        assertThat(validationResponseResource).isNotNull();
        // Assert that only 2 of the 3 policies are validated thanks to PolicySelector filtering the right one to use
        assertThat(evaluatedPolicies.size()).isEqualTo(2);

        // Verify single policies (verify that they match the event type)
        PolicyResource policyResource = policyClient.getPolicyVersion(evaluatedPolicies.get(0).getPolicyId());
        assertThat(policyResource.getEvaluationEvent()).isEqualTo(evaluationRequestResource.getEvent().toString());
        policyResource = policyClient.getPolicyVersion(evaluatedPolicies.get(1).getPolicyId());
        assertThat(policyResource.getEvaluationEvent()).isEqualTo(evaluationRequestResource.getEvent().toString());

        // Verify PolicyEvaluationResults in DB
        ResponseEntity<PagedPolicyEvaluationResultResource> getResponse = policyClient.readAllPolicyEvaluationResultsResponseEntity();
        List<PolicyEvaluationResultResource> policyEvaluationResults = getResponse.getBody().getContent();
        assertThat(policyEvaluationResults.size()).isEqualTo(2);
        assertThat(policyEvaluationResults.get(0).getPolicyId()).isEqualTo(evaluatedPolicies.get(0).getPolicyId());
        assertThat(policyEvaluationResults.get(0).getResult()).isEqualTo(evaluatedPolicies.get(0).getResult());
        assertThat(policyEvaluationResults.get(0).getOutputObject()).isEqualTo(evaluatedPolicies.get(0).getOutputObject());
        assertThat(policyEvaluationResults.get(0).getInputObject()).isEqualTo(evaluatedPolicies.get(0).getInputObject());
        assertThat(policyEvaluationResults.get(0).getDataProductId()).isEqualTo(evaluatedPolicies.get(0).getDataProductId());
        assertThat(policyEvaluationResults.get(0).getDataProductVersion()).isEqualTo(evaluatedPolicies.get(0).getDataProductVersion());
        assertThat(policyEvaluationResults.get(1).getPolicyId()).isEqualTo(evaluatedPolicies.get(1).getPolicyId());
        assertThat(policyEvaluationResults.get(1).getResult()).isEqualTo(evaluatedPolicies.get(1).getResult());
        assertThat(policyEvaluationResults.get(1).getOutputObject()).isEqualTo(evaluatedPolicies.get(1).getOutputObject());
        assertThat(policyEvaluationResults.get(1).getInputObject()).isEqualTo(evaluatedPolicies.get(1).getInputObject());
        assertThat(policyEvaluationResults.get(1).getDataProductId()).isEqualTo(evaluatedPolicies.get(1).getDataProductId());
        assertThat(policyEvaluationResults.get(1).getDataProductVersion()).isEqualTo(evaluatedPolicies.get(1).getDataProductVersion());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testValidateObjectSpELFilteringNotPassed() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_2);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_2, parentEngineResource.getId());
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_3, parentEngineResource.getId());
        PolicyEvaluationRequestResource evaluationRequestResource = createPolicyEvaluationRequestResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_REQUEST_2
        );
        ResponseEntity<ValidationResponseResource> postResponse =
                policyClient.validateInputObjectResponseEntity(evaluationRequestResource);
        verifyResponseEntity(postResponse, HttpStatus.OK, true);
        ValidationResponseResource validationResponseResource = postResponse.getBody();
        List<PolicyEvaluationResultResource> evaluatedPolicies = validationResponseResource.getPolicyResults();

        // Verification
        verifyResponseEntity(postResponse, HttpStatus.OK,true);
        assertThat(validationResponseResource).isNotNull();
        // Assert that no policies are validated thanks to PolicySelector filtering with SpEL expression
        assertThat(evaluatedPolicies.size()).isEqualTo(0);

    }

}
