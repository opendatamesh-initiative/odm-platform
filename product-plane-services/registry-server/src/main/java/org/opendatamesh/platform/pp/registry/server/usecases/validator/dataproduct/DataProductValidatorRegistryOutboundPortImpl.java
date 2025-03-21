package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;

import java.util.Comparator;
import java.util.Optional;

class DataProductValidatorRegistryOutboundPortImpl implements DataProductValidatorRegistryOutboundPort {
    private final DataProductService dataProductService;
    private final DataProductVersionService dataProductVersionService;

    DataProductValidatorRegistryOutboundPortImpl(DataProductService dataProductService, DataProductVersionService dataProductVersionService) {
        this.dataProductService = dataProductService;
        this.dataProductVersionService = dataProductVersionService;
    }

    @Override
    public Optional<DataProduct> findDataProduct(DataProduct dataProduct) {
        return dataProductService.searchDataProducts(dataProduct.getFullyQualifiedName(), dataProduct.getDomain()).stream().findAny();
    }

    @Override
    public Optional<DataProductVersion> findMostRecentDataProductVersion(DataProductVersion dataProductVersion) {
        return dataProductService.searchDataProducts(
                        dataProductVersion.getInfo().getFullyQualifiedName(),
                        dataProductVersion.getInfo().getDomain()
                ).stream()
                .findAny()
                .flatMap(dataProduct ->
                        dataProductVersionService.searchDataProductVersions(dataProduct.getId()).stream()
                                .max(Comparator.comparing(DataProductVersion::getCreatedAt))
                );
    }
}
