package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.dpds.location.UriLocation;
import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

class DataProductValidatorParserOutboundPortImpl implements DataProductValidatorParserOutboundPort {

    private final DataProductService dataProductService;
    private final DataProductVersionService dataProductVersionService;

    DataProductValidatorParserOutboundPortImpl(DataProductService dataProductService, DataProductVersionService dataProductVersionService) {
        this.dataProductService = dataProductService;
        this.dataProductVersionService = dataProductVersionService;
    }

    @Override
    public DataProduct extractDataProductFromRawDescriptor(JsonNode rawDataProductVersion) {
        UriLocation descriptorLocation = new UriLocation(rawDataProductVersion.toString());
        DataProductVersion dataProductVersion = dataProductService.descriptorToDataProductVersion(descriptorLocation, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());
        return dataProductVersionService.getDataProduct(dataProductVersion);
    }

    @Override
    public DataProductVersion extractDataProductVersionFromRawDescriptor(JsonNode dataProductVersion) {
        UriLocation descriptorLocation = new UriLocation(dataProductVersion.toString());
        return dataProductService.descriptorToDataProductVersion(descriptorLocation, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());
    }

    @Override
    public DataProductValidatorResult validateDescriptorSyntax(JsonNode dataProductVersion) {
        //Temporary mocked
        return new DataProductValidatorResult("Syntax Validation", true, null, false);
    }
}
