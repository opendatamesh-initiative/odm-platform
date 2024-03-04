package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractValidationController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationControllerImpl extends AbstractValidationController {

    public PolicyEvaluationResultResource validateObject(PolicyEvaluationRequestResource evaluationRequest) {
        PolicyEvaluationResultResource MOCKED_POLICY_EVAL_RESULT = new PolicyEvaluationResultResource();
        MOCKED_POLICY_EVAL_RESULT.setResult(true);
        return MOCKED_POLICY_EVAL_RESULT;
    }

}
