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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.*;

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
            PolicyEvaluationRequestResource.EventType suite,
            JsonNode inputObject
    ) {
        List<Policy> policySubset = selectPoliciesBySuite(suite);
        policySubset = selectPoliciesBySpELExpression(policySubset, inputObject);
        return policySubset;
    }

    private List<Policy> selectPoliciesBySuite(PolicyEvaluationRequestResource.EventType suite) {

        PolicySearchOptions policySearchOptions = new PolicySearchOptions();
        policySearchOptions.setSuite(suite.toString());

        return policyService.findAllFiltered(Pageable.unpaged(), policySearchOptions).getContent();

    }

    private List<Policy> selectPoliciesBySpELExpression(List<Policy> policies, JsonNode inputObject) {

        // Test expression
        String testSpELExpression = "['afterState']['domain'] == 'sampleDomain' and ['afterState']['domain'] != 'sampleDomainTwo'";
        // Parse SpEL expression
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(testSpELExpression);
        // Create context
        Map<String, Object> context = convertJsonNodeToMap(inputObject);

        // Create a new list to store filtered policies
        List<Policy> filteredPolicies = new ArrayList<>();

        // Iterate over policies and filter them based on the SpEL expression
        for (Policy policy : policies) {
            // Evaluate SpEL expression for each policy
            boolean result = expression.getValue(context, Boolean.class);
            if (result) {
                filteredPolicies.add(policy); // Add the policy if the expression evaluates to true
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

    public static Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = jsonNode.get(fieldName);
            if (fieldValue.isObject()) {
                map.put(fieldName, convertJsonNodeToMap(fieldValue));
            } else {
                map.put(fieldName, fieldValue.asText());
            }
        }
        return map;
    }

}
