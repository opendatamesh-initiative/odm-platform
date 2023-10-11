package org.opendatamesh.platform.pp.blueprint.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public class BlueprintClient extends ODMClient {

    public BlueprintClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    public BlueprintClient(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
    }


    // ----------------------------------------
    // BLUEPRINT endpoints
    // ----------------------------------------

    public ResponseEntity readBlueprints() throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(BlueprintAPIRoutes.BLUEPRINTS),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                BlueprintResource[].class
        );

        return response;
    }

    public ResponseEntity readOneBlueprint(Long id) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrlOfItem(BlueprintAPIRoutes.BLUEPRINTS),
                Object.class,
                id
        );

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                BlueprintResource.class
        );

        return response;
    }

    public ResponseEntity createBlueprint(BlueprintResource policies) throws IOException {

        ResponseEntity postBlueprintResponse = rest.postForEntity(
                apiUrl(BlueprintAPIRoutes.BLUEPRINTS),
                policies,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                postBlueprintResponse,
                HttpStatus.CREATED,
                BlueprintResource.class
        );

        return response;
    }

    public ResponseEntity updateBlueprint(BlueprintResource blueprintResource) throws JsonProcessingException {

        ResponseEntity putBlueprintResponse = rest.exchange(
                apiUrlOfItem(BlueprintAPIRoutes.BLUEPRINTS),
                HttpMethod.PUT,
                new HttpEntity<>(blueprintResource),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                putBlueprintResponse,
                HttpStatus.OK,
                BlueprintResource.class
        );

        return response;
    }

    public ResponseEntity deleteBlueprint(Long id) throws JsonProcessingException {
        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(BlueprintAPIRoutes.BLUEPRINTS),
                HttpMethod.DELETE,
                null,
                Object.class,
                id
        );

        ResponseEntity response = mapResponseEntity(
                deleteResponse,
                HttpStatus.OK,
                Void.class
        );

        return response;
    }



    // ----------------------------------------
    // UTILS
    // ----------------------------------------

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
