package org.opendatamesh.platform.pp.policy.server.services.proxies.policy.engine;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClient;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyEngineClientMock;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"test", "testmysql", "testpostgresql"})
public class PolicyEngineProxyMock extends AbstractPolicyEngineProxy {

    @Override
    protected PolicyEngineClient getPolicyEngineClient(PolicyEngine policyEngine) {
        if(policyEngineClients.containsKey(policyEngine.getName())) {
            return policyEngineClients.get(policyEngine.getName());
        } else {
            PolicyEngineClient policyEngineClient = new PolicyEngineClientMock();
            policyEngineClients.put(
                    policyEngine.getName(),
                    policyEngineClient
            );
            return policyEngineClient;
        }
    }

}
