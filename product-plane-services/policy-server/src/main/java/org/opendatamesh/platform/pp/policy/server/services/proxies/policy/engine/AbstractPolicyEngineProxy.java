package org.opendatamesh.platform.pp.policy.server.services.proxies.policy.engine;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.services.proxies.PolicyEngineProxy;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClient;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPolicyEngineProxy implements PolicyEngineProxy {

    protected static Map<String, PolicyEngineClient> policyEngineClients = new HashMap<>();

    @Override
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

    protected abstract PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine);

}
