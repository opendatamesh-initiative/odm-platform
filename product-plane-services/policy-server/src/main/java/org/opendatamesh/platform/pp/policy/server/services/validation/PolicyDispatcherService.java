package org.opendatamesh.platform.pp.policy.server.services.validation;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.services.PolicyEvaluationResultService;
import org.opendatamesh.platform.pp.policy.server.services.proxies.PolicyEngineProxy;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyDispatcherService {

    @Autowired
    PolicyEvaluationResultService policyEvaluationResultService;

    @Autowired
    PolicyEngineProxy policyEngineProxy;

    public PolicyEvaluationResultResource dispatchPolicy(
            PolicyResource policyToEvaluate,
            PolicyEngine policyEngine,
            PolicyEvaluationResultResource basePolicyEvaluationResult
    ) {

        // Initialize response
        PolicyEvaluationResultResource dispatchResponse = new PolicyEvaluationResultResource();
        dispatchResponse.setPolicyId(basePolicyEvaluationResult.getPolicyId());
        dispatchResponse.setInputObject(basePolicyEvaluationResult.getInputObject());
        dispatchResponse.setDataProductId(basePolicyEvaluationResult.getDataProductId());
        dispatchResponse.setDataProductVersion(basePolicyEvaluationResult.getDataProductVersion());

        // Dispatch validation request
        EvaluationResource validationResponse = policyEngineProxy.validatePolicy(
                basePolicyEvaluationResult.getId(),
                policyToEvaluate,
                policyEngine,
                dispatchResponse.getInputObject()
        );

        // Update response
        dispatchResponse.setResult(validationResponse.getEvaluationResult());
        dispatchResponse.setOutputObject(validationResponse.getOutputObject().toString());

        // Create PolicyEvaluationResult and store it in DB
        dispatchResponse = policyEvaluationResultService.createResource(dispatchResponse);

        return dispatchResponse;

    }

}
