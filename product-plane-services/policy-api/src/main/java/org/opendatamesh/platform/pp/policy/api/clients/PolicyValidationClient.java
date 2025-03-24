package org.opendatamesh.platform.pp.policy.api.clients;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface PolicyValidationClient {

    ValidationResponseResource validateInputObject(PolicyEvaluationRequestResource eventResource);

    ValidationResponseResource testValidateInputObject(@Valid @NotNull @RequestBody PolicyEvaluationRequestResource testRequest);
}
