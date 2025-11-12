package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.proxies.RegistryPolicyServiceProxy;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class DataProductValidatorPolicyOutboundPortImpl implements DataProductValidatorPolicyOutboundPort {

    private final RegistryPolicyServiceProxy policyServiceProxy;
    private final DataProductVersionMapper dataProductVersionMapper;
    private final DataProductMapper dataProductMapper;

    DataProductValidatorPolicyOutboundPortImpl(RegistryPolicyServiceProxy policyServiceProxy, DataProductVersionMapper dataProductVersionMapper, DataProductMapper dataProductMapper) {
        this.policyServiceProxy = policyServiceProxy;
        this.dataProductVersionMapper = dataProductVersionMapper;
        this.dataProductMapper = dataProductMapper;
    }

    @Override
    public List<DataProductValidatorResult> validateDataProductVersionPublish(DataProductVersion dataProductVersion) {

        ValidationResponseResource validationResponseResource = policyServiceProxy.testValidateDataProductVersion(null, dataProductVersionMapper.toResource(dataProductVersion));
        if (validationResponseResource != null && validationResponseResource.getPolicyResults() != null) {
            return policyValidationResponseToDataProductValidationResult(validationResponseResource);
        }
        return List.of();
    }

    @Override
    public List<DataProductValidatorResult> validateDataProductVersionPublish(DataProductVersion mostRecentVersion, DataProductVersion newVersion) {
        ValidationResponseResource validationResponseResource = policyServiceProxy.testValidateDataProductVersion(dataProductVersionMapper.toResource(mostRecentVersion), dataProductVersionMapper.toResource(newVersion));
        if (validationResponseResource != null && validationResponseResource.getPolicyResults() != null) {
            return policyValidationResponseToDataProductValidationResult(validationResponseResource);
        }
        return List.of();
    }

    @Override
    public List<DataProductValidatorResult> validateDataProductUpdate(DataProduct oldDataProduct, DataProduct newDataProduct) {
        ValidationResponseResource validationResponseResource = policyServiceProxy.testValidateDataProduct(dataProductMapper.toResource(oldDataProduct), dataProductMapper.toResource(newDataProduct));
        if (validationResponseResource != null && validationResponseResource.getPolicyResults() != null) {
            return policyValidationResponseToDataProductValidationResult(validationResponseResource);
        }
        return List.of();
    }

    @Override
    public List<DataProductValidatorResult> validateDataProductCreate(DataProduct dataProduct) {
        ValidationResponseResource validationResponseResource = policyServiceProxy.testValidateDataProduct(dataProductMapper.toResource(dataProduct));
        if (validationResponseResource != null && validationResponseResource.getPolicyResults() != null) {
            return policyValidationResponseToDataProductValidationResult(validationResponseResource);
        }
        return List.of();
    }

    private List<DataProductValidatorResult> policyValidationResponseToDataProductValidationResult(ValidationResponseResource validationResponseResource) {
        return validationResponseResource.getPolicyResults().stream()
                .map(policyEvaluationResult -> {
                    try {
                        return new DataProductValidatorResult(
                                policyEvaluationResult.getPolicy().getName(),
                                policyEvaluationResult.getResult(),
                                new ObjectMapper().readTree(policyEvaluationResult.getOutputObject()),
                                policyEvaluationResult.getPolicy().getBlockingFlag()
                        );
                    } catch (JsonProcessingException e) {
                        return new DataProductValidatorResult(
                                policyEvaluationResult.getPolicy().getName(),
                                policyEvaluationResult.getResult(),
                                policyEvaluationResult.getOutputObject(),
                                policyEvaluationResult.getPolicy().getBlockingFlag()
                        );
                    }
                })
                .collect(Collectors.toList());
    }
}
