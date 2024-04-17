package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

public class ValidationErrorsIT extends ODMPolicyIT {

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testValidateObjectWrongInputObject() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationRequestResource evaluationRequestResource = createPolicyEvaluationRequestResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_REQUEST_WRONG_INPUT
        );
        ResponseEntity<ObjectNode> postResponse =
                policyClient.validateInputObjectResponseEntity(evaluationRequestResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_05_MALFORMED_INPUT_OBJECT
        );

    }

}
