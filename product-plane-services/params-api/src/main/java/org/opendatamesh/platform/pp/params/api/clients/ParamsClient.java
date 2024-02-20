package org.opendatamesh.platform.pp.params.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.springframework.http.*;

import java.io.IOException;
import java.util.List;

public class ParamsClient extends ODMClient {

    private String clientUUID;

    public ParamsClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.clientUUID = null;
    }

    public ParamsClient(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
        this.clientUUID = null;
    }

    public ParamsClient(String serverAddress, String clientUUID) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.clientUUID = clientUUID;
    }

    public ParamsClient(String serverAddress, ObjectMapper mapper, String clientUUID) {
        super(serverAddress, mapper);
        this.clientUUID = clientUUID;
    }


    // ======================================================================================
    // Param
    // ======================================================================================


    // ----------------------------------------
    // Create
    // ----------------------------------------

    public ResponseEntity createParam(ParamResource paramResource) throws IOException {

        ResponseEntity postBlueprintResponse = rest.postForEntity(
                apiUrl(ParamsAPIRoutes.PARAMS),
                paramResource,
                Object.class
        );

        ResponseEntity response = mapResponseEntity(
                postBlueprintResponse,
                HttpStatus.CREATED,
                ParamResource.class
        );

        return response;
    }


    // ----------------------------------------
    // Update
    // ----------------------------------------

    public ResponseEntity updateParam(Long paramId, ParamResource paramResource) throws JsonProcessingException {

        ResponseEntity putBlueprintResponse = rest.exchange(
                apiUrlOfItem(ParamsAPIRoutes.PARAMS),
                HttpMethod.PUT,
                new HttpEntity<>(paramResource),
                Object.class,
                paramId
        );

        ResponseEntity response = mapResponseEntity(
                putBlueprintResponse,
                HttpStatus.OK,
                ParamResource.class
        );

        return response;
    }


    // ----------------------------------------
    // Get
    // ----------------------------------------

    public ResponseEntity getParams() throws JsonProcessingException {

        ResponseEntity getResponse;

        if(clientUUID != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("client-UUID", clientUUID);
            getResponse = rest.exchange(
                    apiUrl(ParamsAPIRoutes.PARAMS),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Object.class
            );
        } else {
            getResponse = rest.getForEntity(
                    apiUrl(ParamsAPIRoutes.PARAMS),
                    Object.class
            );
        }

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                ParamResource[].class
        );

        return response;
    }

    public ResponseEntity getOneParam(Long id) throws JsonProcessingException {

        ResponseEntity getResponse;

        if(clientUUID != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("client-UUID", clientUUID);
            getResponse = rest.exchange(
                    apiUrlOfItem(ParamsAPIRoutes.PARAMS),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Object.class,
                    id
            );
        } else {
            getResponse = rest.getForEntity(
                    apiUrlOfItem(ParamsAPIRoutes.PARAMS),
                    Object.class,
                    id
            );
        }

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                ParamResource.class
        );

        return response;
    }

    public ResponseEntity getOneParamByName(String name) throws JsonProcessingException {

        ResponseEntity getResponse;

        if(clientUUID != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("client-UUID", clientUUID);
            getResponse = rest.exchange(
                    apiUrl(ParamsAPIRoutes.PARAMS_FILTER, "?name=" + name),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Object.class
            );
        } else {
            getResponse = rest.getForEntity(
                    apiUrl(ParamsAPIRoutes.PARAMS_FILTER, "?name="+name),
                    Object.class
            );
        }

        ResponseEntity response = mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                ParamResource.class
        );

        return response;
    }


    // ----------------------------------------
    // Delete
    // ----------------------------------------

    public ResponseEntity deleteParam(Long id) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(ParamsAPIRoutes.PARAMS),
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
