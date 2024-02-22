package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
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

    public ResponseEntity createPolicy(PolicyResource policyResource) {
        return null;
    }

    public ResponseEntity updatePolicy(Long id, PolicyResource policyResource) {
        return null;
    }

    public ResponseEntity readAllPolicies() {
        return null;
    }

    public ResponseEntity readOnePolicy(Long id) {
        return null;
    }

    public ResponseEntity deletePolicy(Long id) {
        return null;
    }


    // ----------------------------------------
    // Suite
    // ----------------------------------------

    public ResponseEntity createSuite(PolicyResource policyResource) {
        return null;
    }

    public ResponseEntity updateSuite(Long id, PolicyResource policyResource) {
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

    public ResponseEntity createEngine(PolicyResource policyResource) {
        return null;
    }

    public ResponseEntity updateEngine(Long id, PolicyResource policyResource) {
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
