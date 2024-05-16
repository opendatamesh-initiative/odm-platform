package org.opendatamesh.platform.up.validator.api.controllers;

import org.opendatamesh.platform.up.validator.api.resources.DocumentResource;
import org.opendatamesh.platform.up.validator.api.resources.EvaluationResource;

public interface ValidatorController {

    EvaluationResource evaluateDocument(DocumentResource documentResource);

}
