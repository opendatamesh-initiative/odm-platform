package org.opendatamesh.platform.pp.registry.server.database.mappers;


import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationRequestResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationResponseResource;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct.DataProductValidatorCommand;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct.DataProductValidatorResult;

@Mapper(componentModel = "spring")
public interface DataProductValidationRequestMapper {

    DataProductValidatorCommand toCommand(DataProductValidationRequestResource validationRequestResource);

    DataProductValidationResponseResource.DataProductValidationResult toResultResource(DataProductValidatorResult useCaseResult);
}
