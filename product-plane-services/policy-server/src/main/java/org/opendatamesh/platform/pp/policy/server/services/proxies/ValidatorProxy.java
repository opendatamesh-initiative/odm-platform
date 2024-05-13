package org.opendatamesh.platform.pp.policy.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.up.policy.api.v1.clients.ValidatorClient;
import org.opendatamesh.platform.up.policy.api.v1.clients.ValidatorClientImpl;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.springframework.stereotype.Service;

@Service
public class ValidatorProxy {

    public EvaluationResource validatePolicy(
            Long policyEvaluationResultId,
            PolicyResource policyToEvaluate,
            PolicyEngine policyEngine,
            JsonNode objectToEvaluate
    ) {
        DocumentResource documentResource = new DocumentResource();
        documentResource.setPolicy(policyToEvaluate);
        documentResource.setPolicyEvaluationId(policyEvaluationResultId);
        documentResource.setObjectToEvaluate(objectToEvaluate);
        return getValidatorClient(policyEngine).evaluateDocument(documentResource);
    }

    protected ValidatorClient getValidatorClient(PolicyEngine policyEngine) {
        return new ValidatorClientImpl(policyEngine.getAdapterUrl());
    }

}