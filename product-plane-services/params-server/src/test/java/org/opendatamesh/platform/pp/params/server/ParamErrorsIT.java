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



    // ======================================================================================
    // READ Param
    // ======================================================================================



    // ======================================================================================
    // DELETE Param
    // ======================================================================================

}
