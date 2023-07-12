package org.opendatamesh.platform.up.policy.api.v1.clients;

import org.opendatamesh.platform.pp.registry.api.v1.clients.ODMClient;
import org.opendatamesh.platform.pp.registry.api.v1.clients.Routes;
import org.opendatamesh.platform.up.policy.api.v1.enums.PatchModes;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.SuiteResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.ValidateResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public class PolicyServiceClient extends ODMClient {

    public PolicyServiceClient(String serverAddress) {
        super(serverAddress);
    }

    // ----------------------------------------
    // POLICY endpoints
    // ----------------------------------------

    public ResponseEntity<PolicyResource[]> readPolicies() {

        ResponseEntity<PolicyResource[]> getResponse = rest.getForEntity(
                apiUrl(Routes.POLICYSERVICE_POLICY),
                PolicyResource[].class
        );

        return getResponse;

    }

    public ResponseEntity<PolicyResource> readOnePolicy(String id) {

        ResponseEntity<PolicyResource> getResponse = rest.getForEntity(
                apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
                PolicyResource.class,
                id
        );

        return getResponse;
    }

    public ResponseEntity<PolicyResource> createPolicy(PolicyResource policies){
        ResponseEntity<PolicyResource> postPolicyResponse = rest.postForEntity(
                apiUrl(Routes.POLICYSERVICE_POLICY),
                policies,
                PolicyResource.class
        );
        return postPolicyResponse;
    }

    public ResponseEntity<PolicyResource> updatePolicy(String id,PolicyResource policies){
        ResponseEntity<PolicyResource> putPolicyResponse = rest.exchange(
                apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
                HttpMethod.PUT,
                new HttpEntity<>(policies),
                PolicyResource.class,
                id
        );
        return putPolicyResponse;
    }

    public ResponseEntity<Void> deletePolicy(String id){
        ResponseEntity<Void> deleteResponse = rest.exchange(apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
                HttpMethod.DELETE,
                null,
                Void.class,
                id);
        return deleteResponse;
    }

    // ----------------------------------------
    // SUITE endpoint
    // ----------------------------------------

    // TODO ...
    public ResponseEntity<SuiteResource[]> readSuites(){

        ResponseEntity<SuiteResource[]> getResponse = rest.getForEntity(
                apiUrl(Routes.POLICYSERVICE_SUITE),
                SuiteResource[].class
        );
        return getResponse;
    }

    public ResponseEntity<SuiteResource> readOneSuite(String id){
        ResponseEntity<SuiteResource> getResponse = rest.getForEntity(
                apiUrlOfItem(Routes.POLICYSERVICE_SUITE),
                SuiteResource.class,
                id);
        return getResponse;
    }

    public ResponseEntity<SuiteResource> createSuite(SuiteResource suite){
        ResponseEntity<SuiteResource> postResponse = rest.postForEntity(
                apiUrl(Routes.POLICYSERVICE_SUITE),
                new HttpEntity<> (suite),
                SuiteResource.class
        );
        return postResponse;
    }

    public ResponseEntity<Void> deleteSuite(String id){
        ResponseEntity<Void> deleteResponse = rest.exchange(apiUrlOfItem(Routes.POLICYSERVICE_SUITE),
                HttpMethod.DELETE,
                null,
                Void.class,
                id);
        return deleteResponse;
    }

    public ResponseEntity<SuiteResource> updateSuite(String suiteId, PatchModes mode, String policyId){
        String extension = suiteId+"?mode="+mode.toString()+"&policyId="+policyId;
        ResponseEntity<SuiteResource> putSuiteResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_SUITE,extension),
                HttpMethod.PATCH,
                null,
                SuiteResource.class
        );
        return putSuiteResponse;
    }


    // ----------------------------------------
    // VALIDATION endpoint
    // ----------------------------------------

    // TODO ...
    public ResponseEntity validateDocument(String[] ids, String[] suites, Object document){
        ResponseEntity<Map> validationResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL),
                HttpMethod.POST,
                new HttpEntity<>(document),
                Map.class
        );
        return validationResponse;
    }

    public ResponseEntity validateDocumentByPolicyId(String id, Object document){
        return rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL, "?id={id}"),
                HttpMethod.POST,
                new HttpEntity<> (document),
                ValidateResponse.class,
                id
        );
    }

    public ResponseEntity validateDocumentBySuiteId(String id, Object document){
        return rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL, "?suite={suite}"),
                HttpMethod.POST,
                new HttpEntity<> (document),
                ValidateResponse.class,
                id
        );
    }


}