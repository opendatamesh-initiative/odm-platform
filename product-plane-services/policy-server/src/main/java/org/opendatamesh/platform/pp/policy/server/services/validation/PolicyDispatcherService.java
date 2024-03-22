package org.opendatamesh.platform.pp.policy.server.services.validation;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.services.proxies.PolicyEngineProxy;
import org.opendatamesh.platform.pp.policy.server.services.PolicyEvaluationResultService;
import org.opendatamesh.platform.pp.policy.server.services.mocks.PolicyEngineValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyDispatcherService {

    @Autowired
    PolicyEvaluationResultService policyEvaluationResultService;

    @Autowired
    PolicyEngineProxy policyEngineProxy;

    public PolicyEvaluationResultResource dispatchPolicy(
            Policy policyToEvaluate, PolicyEvaluationResultResource basePolicyEvaluationResult
    ) {

        // Initialize response
        PolicyEvaluationResultResource dispatchResponse = new PolicyEvaluationResultResource();
        dispatchResponse.setPolicyId(basePolicyEvaluationResult.getPolicyId());
        dispatchResponse.setInputObject(basePolicyEvaluationResult.getInputObject());
        dispatchResponse.setDataProductId(basePolicyEvaluationResult.getDataProductId());
        dispatchResponse.setDataProductVersion(basePolicyEvaluationResult.getDataProductVersion());


        // Dispatch validation request
        PolicyEngineValidationResponse validationResponse = policyEngineProxy.validatePolicy(
                policyToEvaluate,
                dispatchResponse.getInputObject().toString() // FIX AFTER REAL POLICY ENGINE ADAPTER
        );

        // Update response
        dispatchResponse.setResult(validationResponse.getResult());
        dispatchResponse.setOutputObject(validationResponse.getOutputObject());

        // Create PolicyEvaluationResult and store it in DB
        dispatchResponse = policyEvaluationResultService.createResource(dispatchResponse);

        return dispatchResponse;

    }

}
