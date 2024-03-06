package org.opendatamesh.platform.pp.policy.api.controllers;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;

public interface PolicyValidationController {

    PolicyEvaluationResultResource validateObject(PolicyEvaluationRequestResource eventResource);

}
