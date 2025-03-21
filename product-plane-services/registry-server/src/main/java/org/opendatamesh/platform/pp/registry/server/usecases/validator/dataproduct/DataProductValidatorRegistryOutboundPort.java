package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;

import java.util.Optional;

interface DataProductValidatorRegistryOutboundPort {
    Optional<DataProduct> findDataProduct(DataProduct dataProduct);

    Optional<DataProductVersion> findMostRecentDataProductVersion(DataProductVersion dataProductVersion);
}
