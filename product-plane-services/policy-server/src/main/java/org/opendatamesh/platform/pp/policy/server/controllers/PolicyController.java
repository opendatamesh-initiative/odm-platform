package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicyController extends AbstractPolicyController {
    private final PolicyResource MOCKED_POLICY = new PolicyResource();
    private final PolicyEvaluationResultResource MOCKED_POLICY_EVALUATION_RESULT = new PolicyEvaluationResultResource();

    @Override
    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        List<PolicyResource> list = Lists.newArrayList(MOCKED_POLICY);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicyResource getPolicy(String uuid) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyResource createPolicy(PolicyResource policy) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyResource modifyPolicy(String uuid, PolicyResource policy) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyResource deletePolicy(String uuid) {
        return MOCKED_POLICY;
    }

    @Override
    public PolicyEvaluationResultResource validateObject(EventResource eventResource) {
        return MOCKED_POLICY_EVALUATION_RESULT;
    }

}
