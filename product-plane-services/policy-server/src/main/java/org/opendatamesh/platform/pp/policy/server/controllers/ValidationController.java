package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractValidationController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public class ValidationController extends AbstractValidationController {

    private final PolicyEvaluationResultResource MOCKED_POLICY_EVAL_RESULT = new PolicyEvaluationResultResource();

    @Override
    public PolicyEvaluationResultResource validateObject(EventResource eventResource) {
        return MOCKED_POLICY_EVAL_RESULT;
    }
}
