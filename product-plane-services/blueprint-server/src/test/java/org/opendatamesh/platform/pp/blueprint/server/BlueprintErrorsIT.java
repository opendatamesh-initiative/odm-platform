package org.opendatamesh.platform.pp.blueprint.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

public class BlueprintErrorsIT extends ODMBlueprintIT {

    // ======================================================================================
    // CREATE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateBlueprint400Errors() throws IOException {

        BlueprintResource blueprintResource = null;

        ResponseEntity<ErrorRes> errorResponse = blueprintClient.createBlueprint(blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                BlueprintApiStandardErrors.SC400_01_BLUEPRINT_IS_EMPTY
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateBlueprint422Errors() throws IOException {

        // INVALID BLUEPRINT: missing repositoryBaseUrl
        BlueprintResource blueprintResource = resourceBuilder.readResourceFromFile(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1,
                BlueprintResource.class
        );

        blueprintResource.setRepositoryBaseUrl(null);

        ResponseEntity<ErrorRes> errorResponse = blueprintClient.createBlueprint(blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                "Blueprint repository URL cannot be null"
        );

        // INVALID BLUEPRINT: missing blueprintRepo
        blueprintResource = resourceBuilder.readResourceFromFile(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1,
                BlueprintResource.class
        );

        blueprintResource.setBlueprintRepo(null);

        errorResponse = blueprintClient.createBlueprint(blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                "Blueprint path inside repository cannot be null"
        );

        // INVALID BLUEPRINT: blueprint already exists
        createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);
        blueprintResource = resourceBuilder.readResourceFromFile(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1,
                BlueprintResource.class
        );
        errorResponse = blueprintClient.createBlueprint(blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                BlueprintApiStandardErrors.SC422_02_BLUEPRINT_ALREADY_EXISTS
        );

    }


    // ======================================================================================
    // READ ONE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneBlueprint404Error() throws JsonProcessingException {

        ResponseEntity<ErrorRes> errorResponse = blueprintClient.readOneBlueprint(1L);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND
        );

    }


    // ======================================================================================
    // UPDATE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateBlueprint400Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = blueprintClient.updateBlueprint(1L, null);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                BlueprintApiStandardErrors.SC400_01_BLUEPRINT_IS_EMPTY
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateBlueprint404Errors() throws IOException {

        BlueprintResource blueprintResource = resourceBuilder.readResourceFromFile(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1,
                BlueprintResource.class
        );
        ResponseEntity<ErrorRes> errorResponse = blueprintClient.updateBlueprint(1L, blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateBlueprint422Errors() throws IOException {

        BlueprintResource oldBlueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);
        Long blueprintId = oldBlueprintResource.getId();

        // INVALID BLUEPRINT: missing repositoryBaseUrl
        BlueprintResource blueprintResource = resourceBuilder.readResourceFromFile(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1_UPDATED,
                BlueprintResource.class
        );

        blueprintResource.setRepositoryBaseUrl(null);

        ResponseEntity<ErrorRes> errorResponse = blueprintClient.updateBlueprint(blueprintId, blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                "Blueprint repository URL cannot be null"
        );

        // INVALID BLUEPRINT: missing blueprintRepo
        blueprintResource = resourceBuilder.readResourceFromFile(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1,
                BlueprintResource.class
        );

        blueprintResource.setBlueprintRepo(null);

        errorResponse = blueprintClient.updateBlueprint(blueprintId, blueprintResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                BlueprintApiStandardErrors.SC422_01_BLUEPRINT_IS_INVALID,
                "Blueprint path inside repository cannot be null"
        );

    }


    // ======================================================================================
    // DELETE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteBlueprint404Errors() throws JsonProcessingException {

        ResponseEntity<ErrorRes> errorResponse = blueprintClient.deleteBlueprint(1L);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                BlueprintApiStandardErrors.SC404_01_BLUEPRINT_NOT_FOUND
        );

    }

}
