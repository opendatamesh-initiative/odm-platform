package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.clients.utils.RestUtils;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PolicyClientImpl extends ODMClient implements PolicyClient {

    private final RestUtils restUtils;

    public PolicyClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
        restUtils = new RestUtils(rest);
    }

    public PolicyClientImpl(String serverAddress, RestTemplate restTemplate, ObjectMapper mapper) {
        super(serverAddress, restTemplate, mapper);
        restUtils = new RestUtils(rest);
    }

    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        return restUtils.getPage(apiUrl(PolicyAPIRoutes.POLICIES), pageable, searchOptions, PolicyResource.class);
    }

    public PolicyResource getPolicy(Long id) {
        return restUtils.get(apiUrlOfItem(PolicyAPIRoutes.POLICIES), id, PolicyResource.class);
    }

    public PolicyResource getPolicyVersion(Long versionId) {
        return restUtils.get(
                apiUrl(PolicyAPIRoutes.POLICIES) + "/versions/{id}",
                versionId,
                PolicyResource.class
        );
    }

    public PolicyResource createPolicy(PolicyResource policy) {
        return restUtils.create(apiUrl(PolicyAPIRoutes.POLICIES), policy, PolicyResource.class);
    }

    public PolicyResource modifyPolicy(Long id, PolicyResource policy) {
        return restUtils.modify(apiUrlOfItem(PolicyAPIRoutes.POLICIES), id, policy, PolicyResource.class);
    }

    public void deletePolicy(Long id) {
        restUtils.delete(apiUrlOfItem(PolicyAPIRoutes.POLICIES), id);
    }

    public Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
        return restUtils.getPage(apiUrl(PolicyAPIRoutes.ENGINES), pageable, searchOptions, PolicyEngineResource.class);
    }

    public PolicyEngineResource getPolicyEngine(Long id) {
        return restUtils.get(apiUrlOfItem(PolicyAPIRoutes.ENGINES), id, PolicyEngineResource.class);
    }

    public PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource) {
        return restUtils.create(apiUrl(PolicyAPIRoutes.ENGINES), policyEngineResource, PolicyEngineResource.class);
    }

    public PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine) {
        return restUtils.modify(apiUrlOfItem(PolicyAPIRoutes.ENGINES), id, policyEngine, PolicyEngineResource.class);
    }

    public void deletePolicyEngine(Long id) {
        restUtils.delete(apiUrlOfItem(PolicyAPIRoutes.ENGINES), id);
    }


    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        return restUtils.getPage(apiUrl(PolicyAPIRoutes.RESULTS), pageable, searchOptions, PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
        return restUtils.get(apiUrlOfItem(PolicyAPIRoutes.RESULTS), id, PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource createPolicyEvaluationResult(PolicyEvaluationResultResource policyEvaluationResult) {
        return restUtils.create(apiUrl(PolicyAPIRoutes.RESULTS), policyEvaluationResult, PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, PolicyEvaluationResultResource policyEvaluationResult) {
        return restUtils.modify(apiUrlOfItem(PolicyAPIRoutes.RESULTS), id, policyEvaluationResult, PolicyEvaluationResultResource.class);
    }

    public void deletePolicyEvaluationResult(Long id) {
        restUtils.delete(apiUrlOfItem(PolicyAPIRoutes.RESULTS), id);
    }

    public ValidationResponseResource validateInputObject(PolicyEvaluationRequestResource evaluationRequest) {
        return restUtils.genericPost(apiUrl(PolicyAPIRoutes.VALIDATION), evaluationRequest, ValidationResponseResource.class);
    }

    //REST METHODS

    public ResponseEntity createPolicyResponseEntity(PolicyResource policyResource) throws JsonProcessingException {

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

    public ResponseEntity updatePolicyResponseEntity(Long policyId, PolicyResource policyResource) throws JsonProcessingException {

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

    public ResponseEntity readAllPoliciesResponseEntity() throws JsonProcessingException {

        ResponseEntity getPoliciesResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPoliciesResponse,
                HttpStatus.OK,
                PagedPolicyResource.class
        );

        return response;

    }

    public ResponseEntity readOnePolicyResponseEntity(Long policyRootId) throws JsonProcessingException {

        ResponseEntity getPolicyResponse = rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                Object.class,
                policyRootId
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;

    }

    public ResponseEntity readOnePolicyVersionResponseEntity(Long policyVersionId) throws JsonProcessingException {

        ResponseEntity getPolicyResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES, "/versions/" + policyVersionId),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;

    }

    public ResponseEntity deletePolicyResponseEntity(Long policyId) throws JsonProcessingException {

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

    public ResponseEntity validateInputObjectResponseEntity(
            PolicyEvaluationRequestResource evaluationRequestResource
    ) throws JsonProcessingException {

        ResponseEntity validateInputObjectResponse = rest.postForEntity(
                apiUrl(PolicyAPIRoutes.VALIDATION),
                evaluationRequestResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                validateInputObjectResponse,
                HttpStatus.OK,
                ValidationResponseResource.class
        );

        return response;

    }

    // ----------------------------------------
    // Engine
    // ----------------------------------------

    public ResponseEntity createPolicyEngineResponseEntity(PolicyEngineResource policyEngineResource) throws JsonProcessingException {

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

    public ResponseEntity updatePolicyEngineResponseEntity(
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

    public ResponseEntity readAllPolicyEnginesResponseEntity() throws JsonProcessingException {

        ResponseEntity getPolicyEnginesResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyEnginesResponse,
                HttpStatus.OK,
                PagedPolicyEngineResource.class
        );

        return response;

    }

    public ResponseEntity readOnePolicyEngineResponseEntity(Long policyEngineId) throws JsonProcessingException {

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

    public ResponseEntity deletePolicyEngineResponseEntity(Long policyEngineId) throws JsonProcessingException {

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

    public ResponseEntity createPolicyEvaluationResultResponseEntity(
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

    public ResponseEntity updatePolicyEvaluationResultResponseEntity(
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

    public ResponseEntity readAllPolicyEvaluationResultsResponseEntity() throws JsonProcessingException {

        ResponseEntity getPolicyEvaluationResultsResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getPolicyEvaluationResultsResponse,
                HttpStatus.OK,
                PagedPolicyEvaluationResultResource.class
        );

        return response;

    }

    public ResponseEntity readOnePolicyEvaluationResultResponseEntity(Long policyEvaluationResultId) throws JsonProcessingException {

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

    public ResponseEntity deletePolicyEvaluationResultResponseEntity(Long policyEvaluationResultId) throws JsonProcessingException {

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

    private ResponseEntity mapResponseEntity(
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
