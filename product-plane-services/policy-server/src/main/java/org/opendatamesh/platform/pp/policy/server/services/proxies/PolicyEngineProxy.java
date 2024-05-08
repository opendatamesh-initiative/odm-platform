package org.opendatamesh.platform.pp.policy.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClient;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClientImpl;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.springframework.stereotype.Service;

@Service
public class PolicyEngineProxy {

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
        return getPolicyEngineClient(policyEngine).evaluateDocument(documentResource);
    }

    protected PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine) {
        return new PolicyEngineClientImpl(policyEngine.getAdapterUrl());
    }

}