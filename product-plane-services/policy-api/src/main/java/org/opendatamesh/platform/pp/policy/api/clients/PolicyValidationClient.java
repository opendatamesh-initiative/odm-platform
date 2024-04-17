package org.opendatamesh.platform.pp.policy.api.clients;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;

public interface PolicyValidationClient {

    ValidationResponseResource validateInputObject(PolicyEvaluationRequestResource eventResource);

}
