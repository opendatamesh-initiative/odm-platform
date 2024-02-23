package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PolicyClient extends ODMClient {

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

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(PolicyAPIRoutes.POLICIES),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                Page.class // Convert to LIST //TODO
        );

        return response;

    }

    public ResponseEntity readOnePolicy(Long policyId) throws JsonProcessingException {
        
        ResponseEntity getResponse = rest.getForEntity(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                Object.class,
                policyId
        );

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;
        
    }

    public ResponseEntity deletePolicy(Long policyId) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(PolicyAPIRoutes.POLICIES),
                HttpMethod.DELETE,
                null,
                Object.class,
                policyId
        );

        ResponseEntity response = mapResponseEntity(
                deleteResponse,
                HttpStatus.OK,
                PolicyResource.class
        );

        return response;
        
    }


    // ----------------------------------------
    // Suite
    // ----------------------------------------

    public ResponseEntity createSuite(PolicySuiteResource policySuiteResource) {
        return null;
    }

    public ResponseEntity updateSuite(Long id, PolicySuiteResource policySuiteResource) {
        return null;
    }

    public ResponseEntity readAllSuites() {
        return null;
    }

    public ResponseEntity readOneSuite(Long id) {
        return null;
    }

    public ResponseEntity deleteSuite(Long id) {
        return null;
    }


    // ----------------------------------------
    // Engine
    // ----------------------------------------

    public ResponseEntity createEngine(PolicyEngineResource policyEngineResource) {
        return null;
    }

    public ResponseEntity updateEngine(Long id, PolicyEngineResource policyEngineResource) {
        return null;
    }

    public ResponseEntity readAllEngine() {
        return null;
    }

    public ResponseEntity readOneEngine(Long id) {
        return null;
    }

    public ResponseEntity deleteEngine(Long id) {
        return null;
    }


    // ----------------------------------------
    // Policy result
    // ----------------------------------------

    public ResponseEntity validatePolicy() {
        return null;
    }

    public ResponseEntity readAllPolicyResults() {
        return null;
    }

    public ResponseEntity readOnePolicyResults(Long policyId) {
        return null;
    }

    public ResponseEntity readOnePolicyResult(Long policyId, Long objectId) {
        return null;
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
