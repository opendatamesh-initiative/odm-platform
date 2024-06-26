package org.opendatamesh.platform.pp.policy.api.clients;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyEngineClient {
    Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions);

    PolicyEngineResource getPolicyEngine(Long id);

    PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource);

    PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine);

    void deletePolicyEngine(Long id);
}
