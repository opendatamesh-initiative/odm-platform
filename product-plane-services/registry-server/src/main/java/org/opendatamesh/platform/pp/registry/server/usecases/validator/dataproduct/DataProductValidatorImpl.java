package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.assertj.core.util.Lists;
import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class DataProductValidatorImpl implements Validator {

    private final DataProductValidatorParserOutboundPort parserOutboundPort;
    private final DataProductValidatorPolicyOutboundPort policyOutboundPort;
    private final DataProductValidatorRegistryOutboundPort registryOutboundPort;

    private final DataProductValidatorCommand command;
    private final DataProductValidatorResultsPresenter presenter;

    DataProductValidatorImpl(DataProductValidatorParserOutboundPort parserOutboundPort, DataProductValidatorPolicyOutboundPort policyOutboundPort, DataProductValidatorRegistryOutboundPort registryOutboundPort, DataProductValidatorCommand command, DataProductValidatorResultsPresenter presenter) {
        this.parserOutboundPort = parserOutboundPort;
        this.policyOutboundPort = policyOutboundPort;
        this.registryOutboundPort = registryOutboundPort;
        this.command = command;
        this.presenter = presenter;
    }

    @Override
    public void validate() {
        try {
            if (Boolean.TRUE.equals(command.getValidateSyntax())) {
                DataProductValidatorResult syntaxValidationResult = parserOutboundPort.validateDescriptorSyntax(command.getDataProductVersion());
                presenter.presentSyntaxValidationResult(syntaxValidationResult);
            }

            if (Boolean.TRUE.equals(command.getValidatePolicies())) {
                DataProduct dataProduct = parserOutboundPort.extractDataProductFromRawDescriptor(command.getDataProductVersion());
                DataProductVersion dataProductVersion = parserOutboundPort.extractDataProductVersionFromRawDescriptor(command.getDataProductVersion());

                List<DataProductValidatorResult> dataProductPoliciesValidationResults = validateDataProductPolicies(dataProduct);
                presenter.presentDataProductValidationResults(dataProductPoliciesValidationResults);

                List<DataProductValidatorResult> dataProductVersionPoliciesValidationResults = validateDataProductVersionPolicies(dataProductVersion);
                presenter.presentDataProductVersionValidationResults(dataProductVersionPoliciesValidationResults);
            }
        } catch (Exception e) {
            presenter.presentError(e);
        }
    }

    private List<DataProductValidatorResult> validateDataProductVersionPolicies(DataProductVersion dataProductVersion) {
        Set<DataProductValidatorPolicyEventType> events = getEventTypes();
        if (!events.isEmpty() && !events.contains(DataProductValidatorPolicyEventType.DATA_PRODUCT_VERSION_CREATION)) {
            return Collections.emptyList();
        }
        Optional<DataProductVersion> mostRecentDataProductVersion = registryOutboundPort.findMostRecentDataProductVersion(dataProductVersion);
        if (mostRecentDataProductVersion.isEmpty()) {
            return policyOutboundPort.validateDataProductVersionPublish(dataProductVersion);
        }
        return policyOutboundPort.validateDataProductVersionPublish(mostRecentDataProductVersion.get(), dataProductVersion);
    }

    private List<DataProductValidatorResult> validateDataProductPolicies(DataProduct dataProduct) {
        Set<DataProductValidatorPolicyEventType> events = getEventTypes();
        Optional<DataProduct> existentDataProduct = registryOutboundPort.findDataProduct(dataProduct);
        if (existentDataProduct.isPresent() && (events.isEmpty() || events.contains(DataProductValidatorPolicyEventType.DATA_PRODUCT_CREATION))) {
            return policyOutboundPort.validateDataProductUpdate(existentDataProduct.get(), dataProduct);
        }
        if (existentDataProduct.isEmpty() && (events.isEmpty() || events.contains(DataProductValidatorPolicyEventType.DATA_PRODUCT_UPDATE))) {
            return policyOutboundPort.validateDataProductCreate(dataProduct);
        }
        return Lists.newArrayList();
    }

    private Set<DataProductValidatorPolicyEventType> getEventTypes() {
        return command.getPolicyEventTypes().stream().map(DataProductValidatorPolicyEventType::valueOf).collect(Collectors.toSet());
    }
}
