package org.opendatamesh.platform.pp.policy.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyMapper;
import org.opendatamesh.platform.pp.policy.server.services.proxies.ValidatorProxy;
import org.opendatamesh.platform.pp.policy.server.services.validation.PolicyEnricherService;
import org.opendatamesh.platform.pp.policy.server.utils.SpELUtils;
import org.opendatamesh.platform.up.validator.api.resources.EvaluationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private ValidatorProxy validatorProxy;

    @Autowired
    private PolicyEvaluationResultService policyEvaluationResultService;

    @Autowired
    private PolicyEnricherService policyEnricherService;

    private static final JsonNodeFactory jsonNodeFactory = ObjectMapperFactory.JSON_MAPPER.getNodeFactory();

    @Transactional
    public ValidationResponseResource validateInput(PolicyEvaluationRequestResource policyEvaluationRequestResource, boolean storeResults) {
        policyEnricherService.enrichRequest(policyEvaluationRequestResource);
        JsonNode inputObject = buildInputObject(policyEvaluationRequestResource);

        // Fetch policy to evaluate
        List<Policy> policiesToEvaluate = selectPolicies(
                policyEvaluationRequestResource.getEvent(),
                inputObject
        );

        ValidationResponseResource response = new ValidationResponseResource();
        response.setResult(true);

        List<PolicyEvaluationResultResource> policyResults = new ArrayList<>();
        for (Policy policyToEvaluate : policiesToEvaluate) {

            PolicyEvaluationResultResource policyEvaluation = new PolicyEvaluationResultResource();
            policyEvaluation.setPolicyId(policyToEvaluate.getId());
            policyEvaluation.setPolicy(policyMapper.toRes(policyToEvaluate));
            policyEvaluation.setDataProductId(policyEvaluationRequestResource.getDataProductId());
            policyEvaluation.setDataProductVersion(policyEvaluationRequestResource.getDataProductVersion());
            policyEvaluation.setInputObject(inputObject);

            EvaluationResource validationResponse = validatorProxy.validatePolicy(
                    policyMapper.toRes(policyToEvaluate),
                    inputObject
            );

            if (Boolean.FALSE.equals(validationResponse.getEvaluationResult())) {
                response.setResult(false);
            }

            policyEvaluation.setResult(validationResponse.getEvaluationResult());
            policyEvaluation.setOutputObject(validationResponse.getOutputObject().toString());

            if (storeResults) {
                policyResults.add(policyEvaluationResultService.createResource(policyEvaluation));
            } else {
                policyResults.add(policyEvaluation);
            }
        }

        response.setPolicyResults(policyResults);

        return response;
    }


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
            if (inputObjSatisfiesPolicyFilteringExpr(inputObject, eventType, policy) ||
                    policyIsWithoutFilteringExpr(policy)
            ) {
                filteredPolicies.add(policy);
            }
        }

        return filteredPolicies;
    }

    private boolean policyIsWithoutFilteringExpr(Policy policy) {
        return !StringUtils.hasText(policy.getFilteringExpression());
    }

    private boolean inputObjSatisfiesPolicyFilteringExpr(JsonNode inputObject, PolicyEvaluationRequestResource.EventType eventType, Policy policy) {
        return StringUtils.hasText(policy.getFilteringExpression()) && SpELUtils.eventObjectMatchesSpelExpression(
                inputObject,
                policy.getFilteringExpression(),
                eventType
        );
    }

    private JsonNode buildInputObject(PolicyEvaluationRequestResource request) {
        ObjectNode inputObject = jsonNodeFactory.objectNode();
        inputObject.put("currentState", request.getCurrentState());
        inputObject.put("afterState", request.getAfterState());
        return inputObject;

    }

}
