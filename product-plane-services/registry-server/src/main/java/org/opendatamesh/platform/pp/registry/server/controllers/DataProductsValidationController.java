package org.opendatamesh.platform.pp.registry.server.controllers;

import org.opendatamesh.platform.pp.registry.api.controllers.AbstractDataProductsValidationController;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationRequestResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationResponseResource;
import org.opendatamesh.platform.pp.registry.server.services.DataProductValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataProductsValidationController implements AbstractDataProductsValidationController {

    @Autowired
    private DataProductValidatorService dataProductValidatorService;

    @Override
    public DataProductValidationResponseResource validateDataProduct(DataProductValidationRequestResource validationRequestResource) {
       return dataProductValidatorService.validateDataProduct(validationRequestResource);
    }
}
