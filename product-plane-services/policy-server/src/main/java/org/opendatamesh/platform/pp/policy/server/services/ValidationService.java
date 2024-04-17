package org.opendatamesh.platform.pp.policy.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyMapper;
import org.opendatamesh.platform.pp.policy.server.services.validation.PolicyDispatcherService;
import org.opendatamesh.platform.pp.policy.server.services.validation.PolicyEnricherService;
import org.opendatamesh.platform.pp.policy.server.utils.SpELUtils;
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
    PolicyMapper policyMapper;

    @Autowired
    PolicyDispatcherService policyDispatcherService;

    @Autowired
    PolicyEnricherService policyEnricherService;

    private static final JsonNodeFactory jsonNodeFactory = ObjectMapperFactory.JSON_MAPPER.getNodeFactory();

    // ======================================================================================
    // Validation
    // ======================================================================================

    public ValidationResponseResource validateInput(PolicyEvaluationRequestResource policyEvaluationRequestResource) {

        // Enrich Request
        policyEvaluationRequestResource = policyEnricherService.enrichRequest(policyEvaluationRequestResource);

        // Initialize response
        ValidationResponseResource response = new ValidationResponseResource();
        List<PolicyEvaluationResultResource> policyResults = new ArrayList<>();
        Boolean validationResult = true;
        PolicyEvaluationResultResource basePolicyResult = initBasePolicyEvaluationResult(policyEvaluationRequestResource);
        PolicyEvaluationResultResource policyResult;

        // Fetch policy to evaluate
        List<Policy> policiesToEvaluate = selectPolicies(
                policyEvaluationRequestResource.getEvent(),
                basePolicyResult.getInputObject()
        );

        // Evaluate policies and update response
        for (Policy policyToEvaluate : policiesToEvaluate) {
            basePolicyResult.setPolicyId(policyToEvaluate.getId());
            policyResult = validateInputSinglePolicy(
                    policyMapper.toRes(policyToEvaluate),
                    policyToEvaluate.getPolicyEngine(),
                    basePolicyResult
            );
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
            PolicyResource policy, PolicyEngine policyEngine, PolicyEvaluationResultResource basePolicyResult
    ) {
        return policyDispatcherService.dispatchPolicy(policy, policyEngine, basePolicyResult);
    }


    // ======================================================================================
    // Policy Selector Method
    // ======================================================================================

    private List<Policy> selectPolicies(
            PolicyEvaluationRequestResource.EventType evaluationEvent,
            JsonNode inputObject
    ) {
        // Default Policy Selection strategy: filter by EVENT
        List<Policy> policySubset = selectPoliciesByEvaluationEvent(evaluationEvent);
        // Custom Policy Selection strategy: evaluate CONDITION of policies on input object
        policySubset = selectPoliciesBySpELExpression(policySubset, inputObject, evaluationEvent);
        return policySubset;
    }

    private List<Policy> selectPoliciesByEvaluationEvent(PolicyEvaluationRequestResource.EventType evaluationEvent) {

        PolicySearchOptions policySearchOptions = new PolicySearchOptions();
        policySearchOptions.setEvaluationEvent(evaluationEvent.toString());

        return policyService.findAllFiltered(Pageable.unpaged(), policySearchOptions).getContent();

    }

    private List<Policy> selectPoliciesBySpELExpression(
            List<Policy> policies, JsonNode inputObject, PolicyEvaluationRequestResource.EventType eventType
    ) {

        // Iterate over policies and filter them based on the SpEL expression
        List<Policy> filteredPolicies = new ArrayList<>();
        for (Policy policy : policies) {
            // Evaluate SpEL expression for each policy
            if(policy.getFilteringExpression() != null) {
                boolean result = SpELUtils.eventObjectMatchesSpelExpression(
                        inputObject,
                        policy.getFilteringExpression(),
                        eventType
                );
                if (result) {
                    // Add the policy if the expression evaluates to true
                    filteredPolicies.add(policy);
                }
            }
        }

        return filteredPolicies;
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
