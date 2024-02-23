package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyEvaluationResultController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicyEvaluationResultController extends AbstractPolicyEvaluationResultController {

    private final PolicyEvaluationResultResource MOCKED_POLICY_EVAL_RESULT = new PolicyEvaluationResultResource();

    @Override
    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        List<PolicyEvaluationResultResource> list = Lists.newArrayList(MOCKED_POLICY_EVAL_RESULT);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicyEvaluationResultResource getPolicyEvaluationResult(String uuid) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

    @Override
    public PolicyEvaluationResultResource createPolicyEvaluationResult(PolicyEvaluationResultResource policyEvaluationResult) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

    @Override
    public PolicyEvaluationResultResource modifyPolicyEvaluationResult(String uuid, PolicyEvaluationResultResource policyEvaluationResult) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

    @Override
    public PolicyEvaluationResultResource deletePolicyEvaluationResult(String uuid) {
        return MOCKED_POLICY_EVAL_RESULT;
    }
}
