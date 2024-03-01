package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
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
    public void testCreatePolicyAllProperties() throws JsonProcessingException {

        // Resources
        EventResource mockEventResource = new EventResource();
        ResponseEntity<PolicyEvaluationResultResource> postResponse =
                policyClient.validateObject(mockEventResource);
        PolicyEvaluationResultResource policyEvaluationResultResource = postResponse.getBody();

        // Verification
        verifyResponseEntity(postResponse, HttpStatus.OK,true);
        assertThat(policyEvaluationResultResource).isNotNull();

    }

}
