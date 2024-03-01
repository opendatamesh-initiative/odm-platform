package org.opendatamesh.platform.pp.policy.api.controllers;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public interface PolicyValidationController {

    PolicyEvaluationResultResource validateObject(EventResource eventResource);

}
