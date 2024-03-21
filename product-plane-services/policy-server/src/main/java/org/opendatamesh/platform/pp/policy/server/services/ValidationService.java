package org.opendatamesh.platform.pp.policy.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.resources.PolicyEvaluationInputObjectResource;
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

    // ======================================================================================
    // Validation
    // ======================================================================================

    public ValidationResponseResource validateInput(PolicyEvaluationRequestResource policyEvaluationRequestResource) {

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
        PolicyEvaluationInputObjectResource inputObjectResource = new PolicyEvaluationInputObjectResource();
        inputObjectResource.setCurrentState(request.getCurrentState());
        inputObjectResource.setAfterState(request.getAfterState());
        String inputObject;
        try {
            inputObject = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(inputObjectResource);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing as JSON inputObject to forward for policy evaluation",
                    e
            );
        }

        policyEvaluationResultResource.setDataProductId(request.getDataProductId());
        policyEvaluationResultResource.setDataProductVersion(request.getDataProductVersion());
        policyEvaluationResultResource.setInputObject(inputObject);

        return policyEvaluationResultResource;

    }

}
