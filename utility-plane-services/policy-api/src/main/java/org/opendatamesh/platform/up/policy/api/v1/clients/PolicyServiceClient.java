package org.opendatamesh.platform.up.policy.api.v1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
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
import java.util.List;
import java.util.Map;

public class PolicyServiceClient extends ODMClient {

    public PolicyServiceClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
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
                PolicyResource[].class);

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
                PolicyResource.class);

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
                PolicyResource.class);
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
                PolicyResource.class);
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
                Void.class);
        return response;
    }

    // ----------------------------------------
    // SUITE endpoint
    // ----------------------------------------

    public ResponseEntity readSuites() throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(Routes.POLICYSERVICE_SUITE),
                Object.class
        );
        ResponseEntity response = mapResponseEntity(getResponse,
                HttpStatus.OK,
                SuiteResource[].class);

        return response;
    }

    public ResponseEntity readOneSuite(String id) throws JsonProcessingException {
        ResponseEntity getResponse = rest.getForEntity(
                apiUrlOfItem(Routes.POLICYSERVICE_SUITE),
                Object.class,
                id
        );
        ResponseEntity response = mapResponseEntity(getResponse,
                HttpStatus.OK,
                SuiteResource.class);
        return response;
    }


    public ResponseEntity createSuite(SuiteResource suite) throws JsonProcessingException {
        ResponseEntity postResponse = rest.postForEntity(
                apiUrl(Routes.POLICYSERVICE_SUITE),
                new HttpEntity<> (suite),
                Object.class
        );
        ResponseEntity response = mapResponseEntity(postResponse,
                HttpStatus.CREATED,
                SuiteResource.class);
        return response;
    }

    public ResponseEntity deleteSuite(String id) throws JsonProcessingException {
        ResponseEntity deleteResponse = rest.exchange(apiUrlOfItem(Routes.POLICYSERVICE_SUITE),
                HttpMethod.DELETE,
                null,
                Object.class,
                id);

        ResponseEntity response = mapResponseEntity(deleteResponse,
                HttpStatus.OK,
                Void.class);
        return response;
    }


    public ResponseEntity updateSuite(String suiteId, PatchModes mode, String policyId) throws JsonProcessingException {
        String extension = "/"+suiteId+"?mode="+mode.toString()+"&policyId="+policyId;
        ResponseEntity putSuiteResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_SUITE,extension),
                HttpMethod.PATCH,
                null,
                Object.class
        );
        ResponseEntity response = mapResponseEntity(putSuiteResponse,
                HttpStatus.OK,
                SuiteResource.class);
        return response;
    }


    // ----------------------------------------
    // VALIDATION endpoint
    // ----------------------------------------

    public ResponseEntity validateDocument(Object document) throws JsonProcessingException {
        ResponseEntity validationResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL),
                HttpMethod.POST,
                new HttpEntity<>(document),
                Object.class
        );
        ResponseEntity response = mapResponseEntity(validationResponse,
                HttpStatus.OK,
                Map.class);
        return response;
    }

    public ResponseEntity validateDocumentByPoliciesIds(Object document, String id) throws JsonProcessingException {
        ResponseEntity validationResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL, "?id={id}"),
                HttpMethod.POST,
                new HttpEntity<> (document),
                Object.class,
                id
        );
        ResponseEntity response = mapResponseEntity(
                validationResponse,
                HttpStatus.OK,
                ValidateResponse.class
        );
        return response;
    }

    public ResponseEntity validateDocumentBySuiteId(Object document, String id) throws JsonProcessingException {
        ResponseEntity validationResponse = rest.exchange(
                apiUrl(Routes.POLICYSERVICE_VALIDATE_BASEURL, "?suite={suite}"),
                HttpMethod.POST,
                new HttpEntity<> (document),
                Object.class,
                id
        );
        ResponseEntity response = mapResponseEntity(validationResponse,
                HttpStatus.OK,
                ValidateResponse.class);
        return response;
    }

    protected ResponseEntity mapResponseEntity(ResponseEntity response,
                                               HttpStatus acceptedStatusCode,
                                               Class acceptedClass) throws JsonProcessingException {
        return mapResponseEntity(response, List.of(acceptedStatusCode),acceptedClass,ErrorResource.class);
    }

}