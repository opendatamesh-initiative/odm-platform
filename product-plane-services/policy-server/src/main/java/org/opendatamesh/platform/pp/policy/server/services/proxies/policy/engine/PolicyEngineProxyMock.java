package org.opendatamesh.platform.pp.policy.server.services.proxies.policy.engine;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClient;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClientMock;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"test", "testmysql", "testpostgresql"})
@Primary
public class PolicyEngineProxyMock extends AbstractPolicyEngineProxy {

    @Override
    protected PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine) {
        PolicyEngineClient policyEngineClient = new PolicyEngineClientMock();
        return policyEngineClient;
    }

}