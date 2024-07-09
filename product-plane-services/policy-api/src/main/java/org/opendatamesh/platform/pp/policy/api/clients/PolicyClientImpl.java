package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PolicyClientImpl extends ODMClient implements PolicyClient, PolicyEngineClient, PolicyEvaluationResultClient, PolicyValidationClient {

    private final RestUtils restUtils;

    public PolicyClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    public PolicyClientImpl(String serverAddress, RestTemplate restTemplate, ObjectMapper mapper) {
        super(serverAddress, restTemplate, mapper);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
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
        return restUtils.put(apiUrlOfItem(PolicyAPIRoutes.POLICIES), id, policy, PolicyResource.class);
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
        return restUtils.put(apiUrlOfItem(PolicyAPIRoutes.ENGINES), id, policyEngine, PolicyEngineResource.class);
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
        return restUtils.put(apiUrlOfItem(PolicyAPIRoutes.RESULTS), id, policyEvaluationResult, PolicyEvaluationResultResource.class);
    }

    public void deletePolicyEvaluationResult(Long id) {
        restUtils.delete(apiUrlOfItem(PolicyAPIRoutes.RESULTS), id);
    }

    public ValidationResponseResource validateInputObject(PolicyEvaluationRequestResource evaluationRequest) {
        return restUtils.genericPost(apiUrl(PolicyAPIRoutes.VALIDATION), evaluationRequest, ValidationResponseResource.class);
    }

    //REST METHODS

    public ResponseEntity<ObjectNode> createPolicyResponseEntity(PolicyResource policyResource) {
        return rest.postForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                policyResource,
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updatePolicyResponseEntity(Long policyId, PolicyResource policyResource) {
        return rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                HttpMethod.PUT,
                new HttpEntity<>(policyResource),
                ObjectNode.class,
                policyId
        );
    }

    public ResponseEntity<ObjectNode> readAllPoliciesResponseEntity() {
        return rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> readOnePolicyResponseEntity(Long policyRootId) {
        return rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                ObjectNode.class,
                policyRootId
        );
    }

    public ResponseEntity<ObjectNode> readOnePolicyVersionResponseEntity(Long policyVersionId) {
        return rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES, "/versions/" + policyVersionId),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> deletePolicyResponseEntity(Long policyId) {
        return rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                policyId
        );
    }

    public ResponseEntity<ObjectNode> validateInputObjectResponseEntity(
            PolicyEvaluationRequestResource evaluationRequestResource
    ) {
        return rest.postForEntity(
                apiUrl(PolicyAPIRoutes.VALIDATION),
                evaluationRequestResource,
                ObjectNode.class
        );
    }

    // ----------------------------------------
    // Engine
    // ----------------------------------------

    public ResponseEntity<ObjectNode> createPolicyEngineResponseEntity(PolicyEngineResource policyEngineResource) {
        return  rest.postForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                policyEngineResource,
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updatePolicyEngineResponseEntity(
            Long policyEngineId, PolicyEngineResource policyEngineResource
    ) {
        return rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.ENGINES),
                HttpMethod.PUT,
                new HttpEntity<>(policyEngineResource),
                ObjectNode.class,
                policyEngineId
        );
    }

    public ResponseEntity<ObjectNode> readAllPolicyEnginesResponseEntity() {
        return rest.getForEntity(
                apiUrl(PolicyAPIRoutes.ENGINES),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> readOnePolicyEngineResponseEntity(Long policyEngineId) {
        return rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.ENGINES),
                ObjectNode.class,
                policyEngineId
        );
    }

    public ResponseEntity<ObjectNode> deletePolicyEngineResponseEntity(Long policyEngineId) {
        return rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.ENGINES),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                policyEngineId
        );
    }


    // ----------------------------------------
    // Policy result
    // ----------------------------------------

    public ResponseEntity<ObjectNode> createPolicyEvaluationResultResponseEntity(
            PolicyEvaluationResultResource policyEvaluationResultResource
    ) {
        return rest.postForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                policyEvaluationResultResource,
                ObjectNode.class
        );
    }



    public ResponseEntity<ObjectNode> updatePolicyEvaluationResultResponseEntity(
            Long policyEvaluationResultId, PolicyEvaluationResultResource policyEvaluationResultResource
    ) {
        return rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.RESULTS),
                HttpMethod.PUT,
                new HttpEntity<>(policyEvaluationResultResource),
                ObjectNode.class,
                policyEvaluationResultId
        );
    }

    public ResponseEntity<ObjectNode> readAllPolicyEvaluationResultsResponseEntity() {
        return rest.getForEntity(
                apiUrl(PolicyAPIRoutes.RESULTS),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> readOnePolicyEvaluationResultResponseEntity(Long policyEvaluationResultId) {
        return rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.RESULTS),
                ObjectNode.class,
                policyEvaluationResultId
        );
    }

    public ResponseEntity<ObjectNode> deletePolicyEvaluationResultResponseEntity(Long policyEvaluationResultId) {
        return rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.RESULTS),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                policyEvaluationResultId
        );
    }

}
