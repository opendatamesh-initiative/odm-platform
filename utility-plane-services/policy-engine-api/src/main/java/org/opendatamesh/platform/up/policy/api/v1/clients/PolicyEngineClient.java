package org.opendatamesh.platform.up.policy.api.v1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class PolicyEngineClient extends ODMClient {

    public PolicyEngineClient(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
    }

    public PolicyEngineClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    public ResponseEntity evaluateDocument(DocumentResource document) throws JsonProcessingException {
        ResponseEntity evaluationResponse = rest.exchange(
                apiUrl(PolicyEngineAPIRoutes.EVALUATE_POLICY),
                HttpMethod.POST,
                new HttpEntity<>(document),
                Object.class
        );
        ResponseEntity response = mapResponseEntity(evaluationResponse,
                HttpStatus.OK,
                EvaluationResource.class);
        return response;
    }

    protected ResponseEntity mapResponseEntity(ResponseEntity response,
                                               HttpStatus acceptedStatusCode,
                                               Class acceptedClass) throws JsonProcessingException {
        return mapResponseEntity(response, List.of(acceptedStatusCode), acceptedClass, ErrorRes.class);
    }

}