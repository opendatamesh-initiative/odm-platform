package org.opendatamesh.platform.up.policy.api.v1.controllers;

import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;

public interface ValidatorController {

    EvaluationResource evaluateDocument(DocumentResource documentResource);

}
