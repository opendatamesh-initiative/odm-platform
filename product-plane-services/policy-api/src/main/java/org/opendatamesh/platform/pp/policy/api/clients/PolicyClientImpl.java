package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.pp.policy.api.clients.utils.RestUtils;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

public class PolicyClientImpl implements PolicyClient {

    private final ODMClient odmClient;
    private final RestUtils restUtils;

    public PolicyClientImpl(String serverAddress, ObjectMapper mapper) {
        odmClient = new ODMClient(serverAddress, mapper);
        restUtils = new RestUtils(odmClient.getRest());
    }

    public PolicyClientImpl(String serverAddress, RestTemplate restTemplate, ObjectMapper mapper) {
        odmClient = new ODMClient(serverAddress, restTemplate, mapper);
        restUtils = new RestUtils(odmClient.getRest());
    }

    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        return restUtils.getPage(odmClient.apiUrl(PolicyAPIRoutes.POLICIES), pageable, searchOptions);
    }

    public PolicyResource getPolicy(Long id) {
        return restUtils.get(odmClient.apiUrlOfItem(PolicyAPIRoutes.POLICIES), id, PolicyResource.class);
    }

    public PolicyResource getPolicyVersion(Long versionId) {
        return restUtils.get(
                odmClient.apiUrl(PolicyAPIRoutes.POLICIES) + "/versions/{id}",
                versionId,
                PolicyResource.class
        );
    }

    public PolicyResource createPolicy(PolicyResource policy) {
        return restUtils.create(odmClient.apiUrl(PolicyAPIRoutes.POLICIES), policy, PolicyResource.class);
    }

    public PolicyResource modifyPolicy(Long id, PolicyResource policy) {
        return restUtils.modify(odmClient.apiUrlOfItem(PolicyAPIRoutes.POLICIES), id, policy, PolicyResource.class);
    }

    public void deletePolicy(Long id) {
        restUtils.delete(odmClient.apiUrlOfItem(PolicyAPIRoutes.POLICIES), id);
    }

    public Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
        return restUtils.getPage(odmClient.apiUrl(PolicyAPIRoutes.ENGINES), pageable, searchOptions);
    }

    public PolicyEngineResource getPolicyEngine(Long id) {
        return restUtils.get(odmClient.apiUrlOfItem(PolicyAPIRoutes.ENGINES), id, PolicyEngineResource.class);
    }

    public PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource) {
        return restUtils.create(odmClient.apiUrl(PolicyAPIRoutes.ENGINES), policyEngineResource, PolicyEngineResource.class);
    }

    public PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine) {
        return restUtils.modify(odmClient.apiUrlOfItem(PolicyAPIRoutes.ENGINES), id, policyEngine, PolicyEngineResource.class);
    }

    public void deletePolicyEngine(Long id) {
        restUtils.delete(odmClient.apiUrlOfItem(PolicyAPIRoutes.ENGINES), id);
    }


    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        return restUtils.getPage(odmClient.apiUrl(PolicyAPIRoutes.RESULTS), pageable, searchOptions);
    }

    public PolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
        return restUtils.get(odmClient.apiUrlOfItem(PolicyAPIRoutes.RESULTS), id, PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource createPolicyEvaluationResult(PolicyEvaluationResultResource policyEvaluationResult) {
        return restUtils.create(odmClient.apiUrl(PolicyAPIRoutes.RESULTS), policyEvaluationResult, PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, PolicyEvaluationResultResource policyEvaluationResult) {
        return restUtils.modify(odmClient.apiUrlOfItem(PolicyAPIRoutes.RESULTS), id, policyEvaluationResult, PolicyEvaluationResultResource.class);
    }

    public void deletePolicyEvaluationResult(Long id) {
        restUtils.delete(odmClient.apiUrlOfItem(PolicyAPIRoutes.RESULTS), id);
    }

    public PolicyEvaluationResultResource validateObject(PolicyEvaluationRequestResource evaluationRequest) {
        return restUtils.genericPost(odmClient.apiUrl(PolicyAPIRoutes.VALIDATION), evaluationRequest, PolicyEvaluationResultResource.class);
    }

}
