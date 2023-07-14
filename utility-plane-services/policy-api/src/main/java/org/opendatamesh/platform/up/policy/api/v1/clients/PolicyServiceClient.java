package org.opendatamesh.platform.up.policy.api.v1.clients;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.up.policy.api.v1.enums.PatchModes;
import org.opendatamesh.platform.up.policy.api.v1.resources.ErrorResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.SuiteResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.ValidateResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public class PolicyServiceClient extends ODMClient {

    public PolicyServiceClient(String serverAddress) {
        super(serverAddress);
    }

    // ----------------------------------------
    // POLICY endpoints
    // ----------------------------------------

    public ResponseEntity readPolicies() throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(Routes.POLICYSERVICE_POLICY),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(getResponse,
                HttpStatus.OK,
                PolicyResource[].class,
                ErrorResource.class);

        return response;
    }

    public ResponseEntity readOnePolicy(String id) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
                Object.class,
                id
        );

        ResponseEntity response = mapResponseEntity(getResponse,
                HttpStatus.OK,
                PolicyResource.class,
                ErrorResource.class);

        return response;
    }

    public ResponseEntity createPolicy(PolicyResource policies) throws IOException {
        ResponseEntity postPolicyResponse = rest.postForEntity(
                apiUrl(Routes.POLICYSERVICE_POLICY),
                policies,
                Object.class
        );
        ResponseEntity response = mapResponseEntity(postPolicyResponse,
                HttpStatus.CREATED,
                PolicyResource.class,
                ErrorResource.class);
        return response;
    }

    public ResponseEntity updatePolicy(String id,PolicyResource policies) throws JsonProcessingException {
        ResponseEntity putPolicyResponse = rest.exchange(
                apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
                HttpMethod.PUT,
                new HttpEntity<>(policies),
                Object.class,
                id
        );

        ResponseEntity response = mapResponseEntity(putPolicyResponse,
                HttpStatus.OK,
                PolicyResource.class,
                ErrorResource.class);
        return response;
    }

    public ResponseEntity deletePolicy(String id) throws JsonProcessingException {
        ResponseEntity deleteResponse = rest.exchange(apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
                HttpMethod.DELETE,
                null,
                Object.class,
                id);

        ResponseEntity response = mapResponseEntity(deleteResponse,
                HttpStatus.OK,
                Void.class,
                ErrorResource.class);
        return response;
    }

    // ----------------------------------------
    // SUITE endpoint
    // ----------------------------------------

    public ResponseEntity readSuites(){

        ResponseEntity<SuiteResource[]> getResponse = rest.getForEntity(
                apiUrl(Routes.POLICYSERVICE_SUITE),
                SuiteResource[].class
        );
        return getResponse;
    }

    public ResponseEntity readOneSuite(String id){
        ResponseEntity<SuiteResource> getResponse = rest.getForEntity(
                apiUrlOfItem(Routes.POLICYSERVICE_SUITE),
                SuiteResource.class,
                id);
        return getResponse;
    }

    public ResponseEntity createSuite(SuiteResource suite){
        ResponseEntity<SuiteResource> postResponse = rest.postForEntity(
                apiUrl(Routes.POLICYSERVICE_SUITE),
                new HttpEntity<> (suite),
                SuiteResource.class
        );
        return postResponse;
    }

    public ResponseEntity deleteSuite(String id){
        ResponseEntity<Void> deleteResponse = rest.exchange(apiUrlOfItem(Routes.POLICYSERVICE_SUITE),
                HttpMethod.DELETE,
                null,
                Void.class,
                id);
        return deleteResponse;
    }

    public ResponseEntity updateSuite(String suiteId, PatchModes mode, String policyId){
        String extension = "/"+suiteId+"?mode="+mode.toString()+"&policyId="+policyId;
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
    //public ResponseEntity validateDocument(String[] ids, String[] suites, Object document){
    public ResponseEntity validateDocument(Object document){
        ResponseEntity<Map> validationResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL),
                HttpMethod.POST,
                new HttpEntity<>(document),
                Map.class
        );
        return validationResponse;
    }

    public ResponseEntity validateDocumentByPoliciesIds(Object document, String id){
        return rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL, "?id={id}"),
                HttpMethod.POST,
                new HttpEntity<> (document),
                ValidateResponse.class,
                id
        );
    }

    public ResponseEntity validateDocumentBySuiteId(Object document, String id){
        return rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL, "?suite={suite}"),
                HttpMethod.POST,
                new HttpEntity<> (document),
                ValidateResponse.class,
                id
        );
    }


}