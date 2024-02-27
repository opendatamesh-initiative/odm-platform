package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PolicyClient extends ODMClient {

    public PolicyClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    public PolicyClient(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
    }

    public PolicyClient(String serverAddress, RestTemplate restTemplate, ObjectMapper mapper) {
        super(serverAddress, restTemplate, mapper);
    }

    // ======================================================================================
    // Proxy services
    // ======================================================================================

    // ----------------------------------------
    // Policy
    // ----------------------------------------

    public ResponseEntity createPolicy(PolicyResource policyResource) throws JsonProcessingException {

        ResponseEntity postPolicyResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                policyResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                postPolicyResponse,
                HttpStatus.CREATED,
                PolicyResource.class
        );

        return response;

    }

    public ResponseEntity updatePolicy(Long policyId, PolicyResource policyResource) throws JsonProcessingException {

        ResponseEntity putPolicyResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                HttpMethod.PUT,
                new HttpEntity<>(policyResource),
                Object.class,
                policyId
        );

        ResponseEntity response = mapResponseEntity(
                putPolicyResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;

    }

    public ResponseEntity readAllPolicies() throws JsonProcessingException {

        ResponseEntity getPoliciesResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPoliciesResponse,
                HttpStatus.OK,
                Page.class // Convert to LIST //TODO
        );

        return response;

    }

    public ResponseEntity readOnePolicy(Long policyId) throws JsonProcessingException {
        
        ResponseEntity getPolicyResponse = rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                Object.class,
                policyId
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;
        
    }

    public ResponseEntity deletePolicy(Long policyId) throws JsonProcessingException {

        ResponseEntity deletePolicyResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                HttpMethod.DELETE,
                null,
                Object.class,
                policyId
        );

        ResponseEntity response = mapResponseEntity(
                deletePolicyResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;
        
    }

    public ResponseEntity validateObject(EventResource eventResource) throws JsonProcessingException {

        ResponseEntity validateObjectResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES, "/*/validate-object"),
                eventResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                validateObjectResponse,
                HttpStatus.OK,
                PolicyEvaluationResultResource.class
        );

        return response;

    }


    // ----------------------------------------
    // Suite
    // ----------------------------------------

    public ResponseEntity createPolicySuite(PolicySuiteResource policySuiteResource) throws JsonProcessingException {

        ResponseEntity postPolicySuiteResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.SUITES),
                policySuiteResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                postPolicySuiteResponse,
                HttpStatus.CREATED,
                PolicySuiteResource.class
        );

        return response;

    }

    public ResponseEntity updatePolicySuite(
            Long policySuiteId, PolicySuiteResource policySuiteResource
    ) throws JsonProcessingException {

        ResponseEntity putPolicySuiteResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.SUITES),
                HttpMethod.PUT,
                new HttpEntity<>(policySuiteResource),
                Object.class,
                policySuiteId
        );

        ResponseEntity response = mapResponseEntity(
                putPolicySuiteResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;

    }

    public ResponseEntity readAllPolicySuites() throws JsonProcessingException {

        ResponseEntity getPolicySuitesResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.SUITES),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPolicySuitesResponse,
                HttpStatus.OK,
                Page.class // Convert to LIST //TODO
        );

        return response;

    }

    public ResponseEntity readOnePolicySuite(Long policySuiteId) throws JsonProcessingException {

        ResponseEntity getPolicySuiteResponse = rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.SUITES),
                Object.class,
                policySuiteId
        );

        ResponseEntity response = mapResponseEntity(
                getPolicySuiteResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;

    }

    public ResponseEntity deletePolicySuite(Long policySuiteId) throws JsonProcessingException {

        ResponseEntity deletePolicySuiteResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.SUITES),
                HttpMethod.DELETE,
                null,
                Object.class,
                policySuiteId
        );

        ResponseEntity response = mapResponseEntity(
                deletePolicySuiteResponse,
                HttpStatus.OK,
                PolicySuiteResource.class
        );

        return response;

    }


    // ----------------------------------------
    // Engine
    // ----------------------------------------

    public ResponseEntity createPolicyEngine(PolicyEngineResource policyEngineResource) throws JsonProcessingException {

        ResponseEntity postPolicyEngineResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                policyEngineResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                postPolicyEngineResponse,
                HttpStatus.CREATED,
                PolicyEngineResource.class
        );

        return response;

    }

    public ResponseEntity updateEngine(
            Long policyEngineId, PolicyEngineResource policyEngineResource
    ) throws JsonProcessingException {

        ResponseEntity putPolicyEngineResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.ENGINES),
                HttpMethod.PUT,
                new HttpEntity<>(policyEngineResource),
                Object.class,
                policyEngineId
        );

        ResponseEntity response = mapResponseEntity(
                putPolicyEngineResponse,
                HttpStatus.OK,
                PolicyEngineResource.class
        );

        return response;

    }

    public ResponseEntity readAllPolicyEngines() throws JsonProcessingException {

        ResponseEntity getPolicyEnginesResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyEnginesResponse,
                HttpStatus.OK,
                Page.class // Convert to LIST //TODO
        );

        return response;

    }

    public ResponseEntity readOnePolicyEngine(Long policyEngineId) throws JsonProcessingException {

        ResponseEntity getPolicyEngineResponse = rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.ENGINES),
                Object.class,
                policyEngineId
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyEngineResponse,
                HttpStatus.OK,
                PolicyEngineResource.class
        );

        return response;

    }

    public ResponseEntity deletePolicyEngine(Long policyEngineId) throws JsonProcessingException {

        ResponseEntity deletePolicyEngineResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.ENGINES),
                HttpMethod.DELETE,
                null,
                Object.class,
                policyEngineId
        );

        ResponseEntity response = mapResponseEntity(
                deletePolicyEngineResponse,
                HttpStatus.OK,
                PolicyEngineResource.class
        );

        return response;

    }


    // ----------------------------------------
    // Policy result
    // ----------------------------------------

    public ResponseEntity createPolicyEvaluationResult(
            PolicyEvaluationResultResource policyEvaluationResultResource
    ) throws JsonProcessingException {

        ResponseEntity postPolicyEvaluationResultResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                policyEvaluationResultResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                postPolicyEvaluationResultResponse,
                HttpStatus.CREATED,
                PolicyEvaluationResultResource.class
        );

        return response;

    }

    public ResponseEntity updatePolicyEvaluationResult(
            Long policyEvaluationResultId, PolicyEvaluationResultResource policyEvaluationResultResource
    ) throws JsonProcessingException {

        ResponseEntity putPolicyEvaluationResultResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.RESULTS),
                HttpMethod.PUT,
                new HttpEntity<>(policyEvaluationResultResource),
                Object.class,
                policyEvaluationResultId
        );

        ResponseEntity response = mapResponseEntity(
                putPolicyEvaluationResultResponse,
                HttpStatus.OK,
                PolicyEvaluationResultResource.class
        );

        return response;

    }

    public ResponseEntity readAllPolicyEvaluationResults() throws JsonProcessingException {

        ResponseEntity getPolicyEvaluationResultsResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyEvaluationResultsResponse,
                HttpStatus.OK,
                Page.class // Convert to LIST //TODO
        );

        return response;

    }

    public ResponseEntity readOnePolicyEvaluationResult(Long policyEvaluationResultId) throws JsonProcessingException {

        ResponseEntity getPolicyEvaluationResultResponse = rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.RESULTS),
                Object.class,
                policyEvaluationResultId
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyEvaluationResultResponse,
                HttpStatus.OK,
                PolicyEvaluationResultResource.class
        );

        return response;

    }

    public ResponseEntity deletePolicyEvaluationResult(Long policyEvaluationResultId) throws JsonProcessingException {

        ResponseEntity deletePolicyEvaluationResultResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.RESULTS),
                HttpMethod.DELETE,
                null,
                Object.class,
                policyEvaluationResultId
        );

        ResponseEntity response = mapResponseEntity(
                deletePolicyEvaluationResultResponse,
                HttpStatus.OK,
                PolicyEvaluationResultResource.class
        );

        return response;

    }


    // ======================================================================================
    // Utils
    // ======================================================================================

    protected ResponseEntity mapResponseEntity(
            ResponseEntity response,
            HttpStatus acceptedStatusCode,
            Class acceptedClass
    ) throws JsonProcessingException {
        return mapResponseEntity(
                response,
                List.of(acceptedStatusCode),
                acceptedClass,
                ErrorRes.class
        );
    }

}
