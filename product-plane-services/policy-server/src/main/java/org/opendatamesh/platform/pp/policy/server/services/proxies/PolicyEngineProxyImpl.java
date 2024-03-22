package org.opendatamesh.platform.pp.policy.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClient;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClientImpl;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("!test & !testmysql & !testpostgresql")
public class PolicyEngineProxyImpl implements PolicyEngineProxy {

    Map<String, PolicyEngineClient> policyEngineClients;

    public PolicyEngineProxyImpl() {
        this.policyEngineClients = new HashMap<>();
    }

    public EvaluationResource validatePolicy(
            Long policyEvaluationResultId,
            PolicyResource policyToEvaluate,
            PolicyEngine policyEngine,
            JsonNode objectToEvaluate
    ) {
        DocumentResource validationRequest = new DocumentResource();
        validationRequest.setPolicy(policyToEvaluate);
        validationRequest.setPolicyEvaluationId(policyEvaluationResultId);
        validationRequest.setObjectToEvaluate(objectToEvaluate);
        EvaluationResource validationResponse = getPolicyEngineClient(policyEngine).evaluateDocument(validationRequest);
        return validationResponse;
    }

    private PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine) {
        if(policyEngineClients.containsKey(policyEngine.getName())) {
            return policyEngineClients.get(policyEngine.getName());
        } else {
            PolicyEngineClient policyEngineClient = new PolicyEngineClientImpl(policyEngine.getAdapterUrl());
            policyEngineClients.put(
                    policyEngine.getName(),
                    policyEngineClient
            );
            return policyEngineClient;
        }
    }

}
