package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PolicyController extends AbstractPolicyController {
    private final PolicyRes MOCKED_POLICY = new PolicyRes();

    @Override
    public Page<PolicyRes> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        List<PolicyRes> list = Lists.newArrayList(MOCKED_POLICY);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicyRes getPolicy(String uuid) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyRes createPolicy(PolicyRes policy) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyRes modifyPolicy(String uuid, PolicyRes policy) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyRes deletePolicy(String uuid) {
        return MOCKED_POLICY;
    }
}
