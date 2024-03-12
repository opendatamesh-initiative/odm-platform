package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractValidationController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.server.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationControllerImpl extends AbstractValidationController {

    @Autowired
    ValidationService service;

    @Override
    public ValidationResponseResource validateInputObject(PolicyEvaluationRequestResource evaluationRequest) {
        return service.validateInput(evaluationRequest);
    }

}
