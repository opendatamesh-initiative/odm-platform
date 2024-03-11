package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    @Autowired
    PolicySelectorService policySelectorService;

    @Autowired
    PolicyDispatcherService policyDispatcherService;

    public ValidationResponseResource validateObject(PolicyEvaluationRequestResource policyEvaluationRequestResource) {

        // Initialize response
        ValidationResponseResource response = new ValidationResponseResource();
        List<PolicyEvaluationResultResource> policyResults = new ArrayList<>();
        Boolean validationResult = true;

        // Extract object to validate
        // TODO: add true logic and remove this part
        String MOCKED_INPUT_OBJECT = "";

        // Fetch policy to evaluate
        List<Policy> policiesToEvaluate = policySelectorService.selectPoliciesBySuite(policyEvaluationRequestResource.getEvent());

        // Evaluate policies and update response
        PolicyEvaluationResultResource policyResult;
        for (Policy policyToEvaluate : policiesToEvaluate) {
            policyResult = validateObjectSinglePolicy(policyToEvaluate, MOCKED_INPUT_OBJECT);
            policyResults.add(policyResult);
            if(!policyResult.getResult() && validationResult) {
                validationResult = false;
            }
        }

        // Update response
        response.setResult(validationResult);
        response.setPolicyResults(policyResults);

        return response;

    }

    private PolicyEvaluationResultResource validateObjectSinglePolicy(Policy policy, String object) {
        return policyDispatcherService.dispatchPolicy(policy, object);
    }

}
