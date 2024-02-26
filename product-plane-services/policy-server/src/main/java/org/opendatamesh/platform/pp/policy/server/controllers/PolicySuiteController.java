package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicySuiteController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicySuiteController extends AbstractPolicySuiteController {

    private final PolicySuiteRes MOCKED_POLICY_SUITE = new PolicySuiteRes();

    @Override
    public Page<PolicySuiteRes> getPolicySuites(Pageable pageable, PolicySuiteSearchOptions searchOptions) {
        List<PolicySuiteRes> list = Lists.newArrayList(MOCKED_POLICY_SUITE);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicySuiteRes getPolicySuite(String uuid) {
        return MOCKED_POLICY_SUITE;
    }

    @Override
    public PolicySuiteRes createPolicySuite(PolicySuiteRes policySuite) {
        return MOCKED_POLICY_SUITE;
    }

    @Override
    public PolicySuiteRes modifyPolicySuite(String uuid, PolicySuiteRes policySuite) {
        return MOCKED_POLICY_SUITE;
    }

    @Override
    public PolicySuiteRes deletePolicySuite(String uuid) {
        return MOCKED_POLICY_SUITE;
    }
}
