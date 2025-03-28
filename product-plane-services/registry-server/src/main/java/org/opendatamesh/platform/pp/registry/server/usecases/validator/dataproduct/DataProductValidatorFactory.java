package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;
import org.opendatamesh.platform.pp.registry.server.services.proxies.RegistryPolicyServiceProxy;
import org.opendatamesh.platform.pp.registry.server.usecases.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataProductValidatorFactory {
    @Autowired
    private DataProductService dataProductService;
    @Autowired
    private DataProductVersionService dataProductVersionService;
    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;
    @Autowired
    private DataProductMapper dataProductMapper;
    @Autowired
    private RegistryPolicyServiceProxy policyServiceProxy;

    public Validator getDataProductValidator(
            DataProductValidatorCommand command,
            DataProductValidatorResultsPresenter presenter
    ) {
        DataProductValidatorParserOutboundPort descriptorParserOutboundPort = new DataProductValidatorParserOutboundPortImpl(dataProductService, dataProductVersionService);
        DataProductValidatorPolicyOutboundPort policyPersistenceOutboundPort = new DataProductValidatorPolicyOutboundPortImpl(policyServiceProxy, dataProductVersionMapper, dataProductMapper);
        DataProductValidatorRegistryOutboundPort registryPersistenceOutboundPort = new DataProductValidatorRegistryOutboundPortImpl(dataProductService, dataProductVersionService);

        return new DataProductValidatorImpl(
                descriptorParserOutboundPort,
                policyPersistenceOutboundPort,
                registryPersistenceOutboundPort,
                command,
                presenter
        );
    }
}
