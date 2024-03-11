package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PolicyDispatcherService {

    @Autowired
    PolicyEvaluationResultService policyEvaluationResultService;

    // MOCK
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();

    public PolicyEvaluationResultResource dispatchPolicy(
            Policy policyToEvaluate, PolicyEvaluationResultResource basePolicyEvaluationResult
    ) {

        // Initialize response
        PolicyEvaluationResultResource dispatchResponse = new PolicyEvaluationResultResource();
        dispatchResponse.setPolicyId(basePolicyEvaluationResult.getPolicyId());
        dispatchResponse.setInputObject(basePolicyEvaluationResult.getInputObject());
        dispatchResponse.setDataProductId(basePolicyEvaluationResult.getDataProductId());
        dispatchResponse.setDataProductVersion(basePolicyEvaluationResult.getDataProductVersion());

        // Dispatch Policy and Input to be evaluated
        // TODO: dispatch the Policy to the right engine instead of using random values
        String outputObject = String.valueOf(random.nextInt());
        Boolean result = random.nextBoolean();
        Integer lenght = 32;
        StringBuilder sb = new StringBuilder(lenght);
        for(int i=0; i<lenght; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));


        // Update response
        dispatchResponse.setResult(result);
        dispatchResponse.setOutputObject(outputObject);

        // Create PolicyEvaluationResult and store it in DB
        dispatchResponse = policyEvaluationResultService.createResource(dispatchResponse);
        // TODO: error handling

        return dispatchResponse;

    }

}
