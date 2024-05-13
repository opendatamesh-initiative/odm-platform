package org.opendatamesh.platform.up.policy.api.v1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.errors.ValidatorApiStandardErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ValidatorClientImpl extends ODMClient implements ValidatorClient {

    public ValidatorClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
    }

    public ValidatorClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }


    // ======================================================================================
    // Evaluate Document
    // ======================================================================================

    public EvaluationResource evaluateDocument(DocumentResource document) {
        ResponseEntity<ObjectNode> evaluationResponse = evaluateDocumentResponseEntity(document);
        return mapResponseOrThrowError(evaluationResponse);
    }

    public ResponseEntity<ObjectNode> evaluateDocumentResponseEntity(DocumentResource document) {
        return rest.exchange(
                apiUrl(ValidatorAPIRoutes.EVALUATE_POLICY),
                HttpMethod.POST,
                new HttpEntity<>(document),
                ObjectNode.class
        );
    }


    // ======================================================================================
    // Utils
    // ======================================================================================

    private EvaluationResource mapResponseOrThrowError(ResponseEntity<ObjectNode> response) {
        if(!response.getStatusCode().is2xxSuccessful()) {
            extractAndThrowError(response);
        }
        try {
            return mapper.treeToValue(response.getBody(), EvaluationResource.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO
        }
    }

    private void extractAndThrowError(ResponseEntity<ObjectNode> response) {
        try {
            ErrorRes errorRes = mapper.treeToValue(response.getBody(), ErrorRes.class);
            ValidatorApiStandardErrors error = ValidatorApiStandardErrors.getByCode(errorRes.getCode());
            switch (response.getStatusCode()) {
                case BAD_REQUEST:
                    throw new BadRequestException(
                            error,
                            errorRes.getMessage()
                    );
                default:
                    throw new InternalServerException(
                            error,
                            errorRes.getMessage()
                    );
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO
        }
    }

}