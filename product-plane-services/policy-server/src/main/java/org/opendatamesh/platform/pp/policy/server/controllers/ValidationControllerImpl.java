package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractValidationController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationControllerImpl extends AbstractValidationController {

    private final PolicyEvaluationResultResource MOCKED_POLICY_EVAL_RESULT = new PolicyEvaluationResultResource();

    public PolicyEvaluationResultResource validateObject(EventResource eventResource) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

}
