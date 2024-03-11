package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEvaluationResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PolicyDispatcherService {

    @Autowired
    PolicyEvaluationResultMapper mapper;

    private Random random = new Random();

    public PolicyEvaluationResultResource dispatchPolicy(Policy policy, String object) {

        PolicyEvaluationResult MOCKED_POLICY_EVAL_RESULT = new PolicyEvaluationResult();
        MOCKED_POLICY_EVAL_RESULT.setResult(random.nextBoolean());

        return mapper.toRes(MOCKED_POLICY_EVAL_RESULT);

    }

}
