package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PolicyDispatcherService {

    @Autowired
    PolicyEvaluationResultService policyEvaluationResultService;

    //TODO: replace Object with PolicyEngineClient
    Map<String, Object> policyEngineClients = new HashMap<>();

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

        // TODO
        //PolicyEngineClient policyEngineClient = getPolicyEngineClient(policyToEvaluate.getPolicyEngine());

        // Dispatch Policy and Input to be evaluated
        // MOCK - TODO: remove it
        String outputObject = String.valueOf(random.nextInt());
        Boolean result = random.nextBoolean();
        Integer lenght = 32;
        StringBuilder sb = new StringBuilder(lenght);
        for(int i=0; i<lenght; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        // TODO: dispatch the Policy to the right engine instead of using random values
        //something = policyEngineClient.validate(dispatchResponse.getInputObject(), policyToEvaluate.getName())
        //result = something.something
        //outputObject = something.somethingelse

        // Update response
        dispatchResponse.setResult(result);
        dispatchResponse.setOutputObject(outputObject);

        // Create PolicyEvaluationResult and store it in DB
        dispatchResponse = policyEvaluationResultService.createResource(dispatchResponse);

        return dispatchResponse;

    }

    // TODO: replace Object with PolicyEngineClient
    private Object getPolicyEngineClient(PolicyEngine policyEngine) {
        if(policyEngineClients.containsKey(policyEngine.getName())) {
            return policyEngineClients.get(policyEngine.getName());
        } else {
            // TODO: replace it with real PolicyEngineClient creation
            Object policyEngineClient = null;
            // PolicyEngineClient policyEngineClient = new PolicyEngineClient(policyEngine.getAdapterUrl());
            policyEngineClients.put(
                    policyEngine.getName(),
                    policyEngineClient
            );
            return policyEngineClient;
        }
    }

}
