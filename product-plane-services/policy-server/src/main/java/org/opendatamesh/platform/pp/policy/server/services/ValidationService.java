package org.opendatamesh.platform.pp.policy.server.services;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    @Autowired
    PolicyService policyService;

    @Autowired
    PolicyDispatcherService policyDispatcherService;

    @Autowired
    PolicyEnricher policyEnricher;

    private static final JsonNodeFactory jsonNodeFactory = ObjectMapperFactory.JSON_MAPPER.getNodeFactory();

    // ======================================================================================
    // Validation
    // ======================================================================================

    public ValidationResponseResource validateInput(PolicyEvaluationRequestResource policyEvaluationRequestResource) {

        // Enrich Request
        policyEvaluationRequestResource = policyEnricher.enrichRequest(policyEvaluationRequestResource);

        // Initialize response
        ValidationResponseResource response = new ValidationResponseResource();
        List<PolicyEvaluationResultResource> policyResults = new ArrayList<>();
        Boolean validationResult = true;

        // Fetch policy to evaluate
        List<Policy> policiesToEvaluate = selectPoliciesBySuite(policyEvaluationRequestResource.getEvent());

        // Evaluate policies and update response
        PolicyEvaluationResultResource basePolicyResult = initBasePolicyEvaluationResult(policyEvaluationRequestResource);
        PolicyEvaluationResultResource policyResult;
        for (Policy policyToEvaluate : policiesToEvaluate) {
            basePolicyResult.setPolicyId(policyToEvaluate.getId());
            policyResult = validateInputSinglePolicy(policyToEvaluate, basePolicyResult);
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

    private PolicyEvaluationResultResource validateInputSinglePolicy(
            Policy policy, PolicyEvaluationResultResource basePolicyResult
    ) {
        return policyDispatcherService.dispatchPolicy(policy, basePolicyResult);
    }


    // ======================================================================================
    // Policy Selector Method
    // ======================================================================================

    private List<Policy> selectPoliciesBySuite(PolicyEvaluationRequestResource.EventType suite) {

        PolicySearchOptions policySearchOptions = new PolicySearchOptions();
        policySearchOptions.setSuite(suite.toString());

        return policyService.findAllFiltered(Pageable.unpaged(), policySearchOptions).getContent();

    }


    // ======================================================================================
    // Utils
    // ======================================================================================

    private PolicyEvaluationResultResource initBasePolicyEvaluationResult(PolicyEvaluationRequestResource request) {

        PolicyEvaluationResultResource policyEvaluationResultResource = new PolicyEvaluationResultResource();

        // Extract object to validate
        ObjectNode inputObject = jsonNodeFactory.objectNode();
        inputObject.put("currentState", request.getCurrentState());
        inputObject.put("afterState", request.getAfterState());

        policyEvaluationResultResource.setDataProductId(request.getDataProductId());
        policyEvaluationResultResource.setDataProductVersion(request.getDataProductVersion());
        policyEvaluationResultResource.setInputObject(inputObject);

        return policyEvaluationResultResource;

    }

}
