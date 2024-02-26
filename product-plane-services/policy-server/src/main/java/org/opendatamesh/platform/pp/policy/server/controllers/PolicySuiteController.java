package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicySuiteController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicySuiteController extends AbstractPolicySuiteController {

    private final PolicySuiteResource MOCKED_POLICY_SUITE = new PolicySuiteResource();

    @Override
    public Page<PolicySuiteResource> getPolicySuites(Pageable pageable, PolicySuiteSearchOptions searchOptions) {
        List<PolicySuiteResource> list = Lists.newArrayList(MOCKED_POLICY_SUITE);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicySuiteResource getPolicySuite(String uuid) {
        return MOCKED_POLICY_SUITE;
    }

    @Override
    public PolicySuiteResource createPolicySuite(PolicySuiteResource policySuite) {
        return MOCKED_POLICY_SUITE;
    }

    @Override
    public PolicySuiteResource modifyPolicySuite(String uuid, PolicySuiteResource policySuite) {
        return MOCKED_POLICY_SUITE;
    }

    @Override
    public PolicySuiteResource deletePolicySuite(String uuid) {
        return MOCKED_POLICY_SUITE;
    }
}
