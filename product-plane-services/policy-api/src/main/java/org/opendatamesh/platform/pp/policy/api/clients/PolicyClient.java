package org.opendatamesh.platform.pp.policy.api.clients;

import org.opendatamesh.platform.pp.policy.api.controllers.PolicyController;
import org.opendatamesh.platform.pp.policy.api.controllers.PolicyEngineController;
import org.opendatamesh.platform.pp.policy.api.controllers.PolicyEvaluationResultController;
import org.opendatamesh.platform.pp.policy.api.controllers.PolicyValidationController;

public interface PolicyClient extends PolicyController, PolicyEngineController, PolicyEvaluationResultController, PolicyValidationController {
}
