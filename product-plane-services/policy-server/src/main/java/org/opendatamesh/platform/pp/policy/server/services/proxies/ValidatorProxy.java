package org.opendatamesh.platform.pp.policy.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.up.validator.api.clients.ValidatorClientImpl;
import org.opendatamesh.platform.up.validator.api.resources.DocumentResource;
import org.opendatamesh.platform.up.validator.api.resources.EvaluationResource;
import org.springframework.stereotype.Service;

@Service
public class ValidatorProxy {

    public EvaluationResource validatePolicy(
            PolicyResource policyToEvaluate,
            JsonNode objectToEvaluate
    ) {
        DocumentResource documentResource = new DocumentResource();
        documentResource.setPolicy(policyToEvaluate);
        documentResource.setObjectToEvaluate(objectToEvaluate);

        return new ValidatorClientImpl(policyToEvaluate.getPolicyEngine().getAdapterUrl())
                .evaluateDocument(documentResource);
    }

}