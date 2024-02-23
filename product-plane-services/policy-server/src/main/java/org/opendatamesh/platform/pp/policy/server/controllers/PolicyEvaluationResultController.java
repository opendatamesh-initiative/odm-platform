package org.opendatamesh.platform.pp.policy.server.controllers;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyEvaluationResultController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PolicyEvaluationResultController extends AbstractPolicyEvaluationResultController {

    private final PolicyEvaluationResultRes MOCKED_POLICY_EVAL_RESULT = new PolicyEvaluationResultRes();

    @Override
    public Page<PolicyEvaluationResultRes> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        List<PolicyEvaluationResultRes> list = Lists.newArrayList(MOCKED_POLICY_EVAL_RESULT);
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    public PolicyEvaluationResultRes getPolicyEvaluationResult(String uuid) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

    @Override
    public PolicyEvaluationResultRes createPolicyEvaluationResult(PolicyEvaluationResultRes policyEvaluationResult) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

    @Override
    public PolicyEvaluationResultRes modifyPolicyEvaluationResult(String uuid, PolicyEvaluationResultRes policyEvaluationResult) {
        return MOCKED_POLICY_EVAL_RESULT;
    }

    @Override
    public PolicyEvaluationResultRes deletePolicyEvaluationResult(String uuid) {
        return MOCKED_POLICY_EVAL_RESULT;
    }
}
