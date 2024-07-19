package org.opendatamesh.platform.pp.params.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.opendatamesh.platform.pp.params.api.resources.ParamsApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

public class ParamErrorsIT extends ODMParamsIT {

    // ======================================================================================
    // CREATE Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateParamError400xx() throws IOException {

        // Resources
        ParamResource paramResource = null;

        // 40001 - Param is empty
        ResponseEntity<ErrorRes> errorResponse = paramsClient.createParam(paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                ParamsApiStandardErrors.SC400_01_PARAM_IS_EMPTY,
                "Param object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateParamError422xx() throws IOException {

        // Resources
        ParamResource paramResource = createParamResource(ODMParamsResources.RESOURCE_PARAM_1);
        String paramName = paramResource.getParamName();
        String paramValue = paramResource.getParamValue();
        ResponseEntity<ErrorRes> errorResponse;

        // 42201 - Parameter is invalid
        // 42201 - Parameter is invalid - Param name cannot be null
        paramResource.setParamName(null);
        errorResponse = paramsClient.createParam(paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                "Param name cannot be null"
        );
        // 42201 - Parameter is invalid - Param value cannot be null
        paramResource.setParamName(paramName);
        paramResource.setParamValue(null);
        errorResponse = paramsClient.createParam(paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                "Param value cannot be null"
        );

        // 42202 - Parameter already exists
        paramResource.setParamValue(paramValue);
        createParam(paramResource);
        errorResponse = paramsClient.createParam(paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ParamsApiStandardErrors.SC422_02_PARAMETER_ALREADY_EXISTS
        );

    }


    // ======================================================================================
    // UPDATE Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateParamError400xx() throws IOException {

        // Resources
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        Long paramResourceId = paramResource.getId();

        // 40001 - Param is empty
        ResponseEntity<ErrorRes> errorResponse = paramsClient.updateParam(paramResourceId, null);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                ParamsApiStandardErrors.SC400_01_PARAM_IS_EMPTY
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateParamError404xx() throws IOException {

        // Resources
        ParamResource paramResource = createParamResource(ODMParamsResources.RESOURCE_PARAM_1);

        // 40401 - Param not found
        ResponseEntity<ErrorRes> errorResponse = paramsClient.updateParam(1L, paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateParamError422xx() throws IOException {

        // Resources
        ParamResource paramResource = createParam(ODMParamsResources.RESOURCE_PARAM_1);
        Long paramResourceId = paramResource.getId();
        String paramResourceName = paramResource.getParamName();
        ResponseEntity<ErrorRes> errorResponse;

        // 42201 - Parameter is invalid
        // 42201 - Parameter is invalid - Param name cannot be null
        paramResource.setParamName(null);
        errorResponse = paramsClient.updateParam(paramResourceId, paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                "Param name cannot be null"
        );

        // 42201 - Parameter is invalid - Param value cannot be null
        paramResource.setParamName(paramResourceName);
        paramResource.setParamValue(null);
        errorResponse = paramsClient.updateParam(paramResourceId, paramResource);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ParamsApiStandardErrors.SC422_01_PARAMETER_IS_INVALID,
                "Param value cannot be null"
        );

    }


    // ======================================================================================
    // READ Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneParamError404xx() throws IOException {

        // Resources
        // Nothing, so the getOne will fail

        // 40401 - Parameter not found - Param with id [" + paramId + "] does not exist
        ResponseEntity<ErrorRes> errorResponse = paramsClient.getOneParam(1L);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND,
                "Param with id [1] does not exist"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneParamByNameError404xx() throws IOException {

        // Resources
        // Nothing, so the getOne will fail

        // 40401 - Parameter not found - Param with name [" + paramName + "] does not exist
        ResponseEntity<ErrorRes> errorResponse = paramsClient.getOneParamByName("spring.port");
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND,
                "Param with name [spring.port] does not exist"
        );

    }

    // ======================================================================================
    // DELETE Param
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteParamError404xx() throws IOException {

        // Resources
        // Nothing, so the deletion will fail

        // 40401 - Parameter not found
        ResponseEntity<ErrorRes> errorResponse = paramsClient.deleteParam(1L);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                ParamsApiStandardErrors.SC404_01_PARAMETER_NOT_FOUND
        );

    }

}
