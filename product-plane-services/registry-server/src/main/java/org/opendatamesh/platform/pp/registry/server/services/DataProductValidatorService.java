package org.opendatamesh.platform.pp.registry.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationRequestResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationResponseResource;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductValidationRequestMapper;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct.DataProductValidatorCommand;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct.DataProductValidatorFactory;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct.DataProductValidatorResult;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct.DataProductValidatorResultsPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataProductValidatorService {
    @Autowired
    private DataProductValidatorFactory dataProductValidatorFactory;
    @Autowired
    private DataProductValidationRequestMapper mapper;

    public DataProductValidationResponseResource validateDataProduct(
            DataProductValidationRequestResource validationRequestResource) {
        DataProductValidatorCommand command = mapper.toCommand(validationRequestResource);

        DataProductValidationResponseResource validationResponseResource = new DataProductValidationResponseResource();
        DataProductValidatorResultsPresenter presenter = buildResponsePresenter(validationResponseResource);

        dataProductValidatorFactory.getDataProductValidator(command, presenter).validate();
        return validationResponseResource;
    }

    private DataProductValidatorResultsPresenter buildResponsePresenter(DataProductValidationResponseResource validationResponseResource) {
        return new DataProductValidatorResultsPresenter() {
            @Override
            public void presentSyntaxValidationResult(DataProductValidatorResult syntaxValidationResult) {
                validationResponseResource.setSyntaxValidationResult(mapper.toResultResource(syntaxValidationResult));
            }

            @Override
            public void presentDataProductValidationResults(List<DataProductValidatorResult> dataProductPoliciesValidationResults) {
                dataProductPoliciesValidationResults.forEach(
                        dataProductValidatorResult -> validationResponseResource.getPoliciesValidationResults().put(dataProductValidatorResult.getName(), mapper.toResultResource(dataProductValidatorResult))
                );
            }

            @Override
            public void presentDataProductVersionValidationResults(List<DataProductValidatorResult> dataProductVersionPoliciesValidationResults) {
                dataProductVersionPoliciesValidationResults.forEach(
                        dataProductValidatorResult -> validationResponseResource.getPoliciesValidationResults().put(dataProductValidatorResult.getName(), mapper.toResultResource(dataProductValidatorResult))
                );
            }

            @Override
            public void presentError(Exception e) {
                if (e instanceof UnprocessableEntityException) {
                    validationResponseResource.setSyntaxValidationResult(new DataProductValidationResponseResource.DataProductValidationResult(
                            false,
                            e.getMessage()
                    ));
                } else {
                    throw new InternalServerException(e);
                }
            }
        };
    }

}
