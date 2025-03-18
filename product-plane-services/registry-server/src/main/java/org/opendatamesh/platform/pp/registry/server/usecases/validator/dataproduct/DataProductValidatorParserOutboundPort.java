package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;

interface DataProductValidatorParserOutboundPort {
    DataProduct extractDataProductFromRawDescriptor(JsonNode dataProductVersion);

    DataProductVersion extractDataProductVersionFromRawDescriptor(JsonNode dataProductVersion);

    DataProductValidatorResult validateDescriptorSyntax(JsonNode dataProductVersion);
}
