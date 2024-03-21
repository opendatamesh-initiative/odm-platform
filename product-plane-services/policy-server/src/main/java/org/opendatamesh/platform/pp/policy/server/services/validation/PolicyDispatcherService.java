package org.opendatamesh.platform.pp.policy.server.services.validation;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.services.PolicyEvaluationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PolicyDispatcherService {

    @Autowired
    PolicyEvaluationResultService policyEvaluationResultService;

    //private policyEngineClient;

    // MOCK
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();
    // END MOCK

    public PolicyEvaluationResultResource dispatchPolicy(
            Policy policyToEvaluate, PolicyEvaluationResultResource basePolicyEvaluationResult
    ) {

        // Initialize response
        PolicyEvaluationResultResource dispatchResponse = new PolicyEvaluationResultResource();
        dispatchResponse.setPolicyId(basePolicyEvaluationResult.getPolicyId());
        dispatchResponse.setInputObject(basePolicyEvaluationResult.getInputObject());
        dispatchResponse.setDataProductId(basePolicyEvaluationResult.getDataProductId());
        dispatchResponse.setDataProductVersion(basePolicyEvaluationResult.getDataProductVersion());

        // TODO:
        //  get the selected client / re-configure the same client (a generic PolicyEngineClient)
        //  from the policyToEvaluate.getPolicyEngine()
        PolicyEngine policyEngine = policyToEvaluate.getPolicyEngine();
        //policyEngineClient = new PolicyEngineClient(policyEngine.getAdapterUrl())

        // Dispatch Policy and Input to be evaluated
        // TODO: dispatch the Policy to the right engine instead of using random values
        String outputObject = String.valueOf(random.nextInt());
        Boolean result = random.nextBoolean();
        Integer lenght = 32;
        StringBuilder sb = new StringBuilder(lenght);
        for(int i=0; i<lenght; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        //something = policyEngineClient.validate(dispatchResponse.getInputObject(), policyToEvaluate.getName())
        //result = something.something
        //outputObject = something.somethingelse

        // Update response
        dispatchResponse.setResult(result);
        dispatchResponse.setOutputObject(outputObject);

        // Create PolicyEvaluationResult and store it in DB
        dispatchResponse = policyEvaluationResultService.createResource(dispatchResponse);
        // TODO: error handling

        return dispatchResponse;

    }

}
