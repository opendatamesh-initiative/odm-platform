package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;

import java.util.List;

interface DataProductValidatorPolicyOutboundPort {
    List<DataProductValidatorResult> validateDataProductVersionPublish(DataProductVersion dataProductVersion);

    List<DataProductValidatorResult> validateDataProductVersionPublish(DataProductVersion dataProductVersion, DataProductVersion dataProductVersion1);

    List<DataProductValidatorResult> validateDataProductUpdate(DataProduct dataProduct, DataProduct dataProduct1);

    List<DataProductValidatorResult> validateDataProductCreate(DataProduct dataProduct);
}
