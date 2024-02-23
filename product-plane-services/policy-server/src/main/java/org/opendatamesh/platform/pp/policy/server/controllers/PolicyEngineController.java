package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyEngineController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicyEngineController extends AbstractPolicyEngineController {

    private final PolicyEngineRes MOCKED_POLICY_ENGINE = new PolicyEngineRes();

    @Override
    public Page<PolicyEngineRes> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
        List<PolicyEngineRes> list = Lists.newArrayList(MOCKED_POLICY_ENGINE);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicyEngineRes getPolicyEngine(String uuid) {
        return MOCKED_POLICY_ENGINE;
    }

    @Override
    public PolicyEngineRes createPolicyEngine(PolicyEngineRes policyEngine) {
        return MOCKED_POLICY_ENGINE;
    }

    @Override
    public PolicyEngineRes modifyPolicyEngine(String uuid, PolicyEngineRes policyEngine) {
        return MOCKED_POLICY_ENGINE;
    }

    @Override
    public PolicyEngineRes deletePolicyEngine(String uuid) {
        return MOCKED_POLICY_ENGINE;
    }
}
