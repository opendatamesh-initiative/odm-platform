package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.services.mocks.PolicyEngineClient;
import org.opendatamesh.platform.pp.policy.server.services.mocks.PolicyEngineValidationRequest;
import org.opendatamesh.platform.pp.policy.server.services.mocks.PolicyEngineValidationResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PolicyEngineProxy {

    Map<String, PolicyEngineClient> policyEngineClients;

    public PolicyEngineProxy() {
        this.policyEngineClients = new HashMap<>();
    }

    public PolicyEngineValidationResponse validatePolicy(
            Policy policyToEvaluate, String objectToEvaluate
    ) {
        PolicyEngineValidationRequest validationRequest = new PolicyEngineValidationRequest();
        validationRequest.setPolicy(policyToEvaluate);
        validationRequest.setInputObject(objectToEvaluate);
        PolicyEngineValidationResponse validationResponse =
                getPolicyEngineClient(policyToEvaluate.getPolicyEngine()).validatePolicy(validationRequest);
        return validationResponse;
    }

    private PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine) {
        if(policyEngineClients.containsKey(policyEngine.getName())) {
            return policyEngineClients.get(policyEngine.getName());
        } else {
            PolicyEngineClient policyEngineClient = new PolicyEngineClient(policyEngine.getAdapterUrl());
            policyEngineClients.put(
                    policyEngine.getName(),
                    policyEngineClient
            );
            return policyEngineClient;
        }
    }

}
