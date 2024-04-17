package org.opendatamesh.platform.pp.policy.server.services.proxies.policy.engine;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClient;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClientImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class PolicyEngineProxyImpl extends AbstractPolicyEngineProxy {

    @Override
    protected PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine) {
        PolicyEngineClient policyEngineClient = new PolicyEngineClientImpl(policyEngine.getAdapterUrl());
        return policyEngineClient;
    }

}
