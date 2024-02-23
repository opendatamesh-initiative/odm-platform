package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyEngineController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicyEngineController extends AbstractPolicyEngineController {

    private final PolicyEngineResource MOCKED_POLICY_ENGINE = new PolicyEngineResource();

    @Override
    public Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
        List<PolicyEngineResource> list = Lists.newArrayList(MOCKED_POLICY_ENGINE);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicyEngineResource getPolicyEngine(String uuid) {
        return MOCKED_POLICY_ENGINE;
    }

    @Override
    public PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngine) {
        return MOCKED_POLICY_ENGINE;
    }

    @Override
    public PolicyEngineResource modifyPolicyEngine(String uuid, PolicyEngineResource policyEngine) {
        return MOCKED_POLICY_ENGINE;
    }

    @Override
    public PolicyEngineResource deletePolicyEngine(String uuid) {
        return MOCKED_POLICY_ENGINE;
    }
}
