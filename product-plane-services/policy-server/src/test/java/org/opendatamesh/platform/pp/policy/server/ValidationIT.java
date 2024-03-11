package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationIT extends ODMPolicyIT {

    // ======================================================================================
    // VALIDATE Object
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testValidateObject() throws JsonProcessingException {

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
                policyClient.validateObjectResponseEntity(evaluationRequestResource);
        ValidationResponseResource validationResponseResource = postResponse.getBody();

        // Verification
        verifyResponseEntity(postResponse, HttpStatus.OK,true);
        assertThat(validationResponseResource).isNotNull();
        // Assert that only 2 of the 3 policies are validated thanks to PolicySelector filtering the right one to use
        assertThat(validationResponseResource.getPolicyResults().size()).isEqualTo(2);

    }

}
