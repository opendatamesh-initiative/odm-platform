package org.opendatamesh.platform.pp.policy.api.controllers;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;

public interface PolicyValidationController {

    ValidationResponseResource validateObject(PolicyEvaluationRequestResource eventResource);

}
