package org.opendatamesh.platform.pp.registry.server.controllers;

import java.util.List;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractDataProductController;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataProductController extends AbstractDataProductController {
    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductMapper dataProductMapper;

    private static final Logger logger = LoggerFactory.getLogger(DataProductController.class);

    public DataProductController() {
        logger.debug("Data product controller succesfully started");
    }

    @Override
    public DataProductResource createDataProduct(
            DataProductResource dataProductRes) {
        if (dataProductRes == null) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_10_PRODUCT_IS_EMPTY,
                    "Data product cannot be empty");
        }
        DataProduct dataProduct = dataProductMapper.toEntity(dataProductRes);
        dataProduct = dataProductService.createDataProduct(dataProduct);
        return dataProductMapper.toResource(dataProduct);
    }

    @Override
    public List<DataProductResource> getDataProducts(
            String fqn, String domain) 
    {
        List<DataProduct> dataProducts = null;

        if (fqn != null || domain != null) {
            dataProducts = dataProductService.searchDataProducts(fqn, domain);
        } else {
            dataProducts = dataProductService.readAllDataProducts();
        }

        return dataProductMapper.toResources(dataProducts);
    }

    @Override
    public DataProductResource getDataProduct(String id) {
        DataProduct dataProduct = dataProductService.readDataProduct(id);
        DataProductResource dataProductResource = dataProductMapper.toResource(dataProduct);
        return dataProductResource;
    }

    @Override
    public DataProductResource updateProduct(DataProductResource dataProductRes) {

        if (dataProductRes == null)
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_10_PRODUCT_IS_EMPTY,
                    "Data Product Descriptor is empty");

        DataProduct dataProduct = dataProductMapper.toEntity(dataProductRes);
        dataProduct = dataProductService.updateDataProduct(dataProduct);

        return dataProductMapper.toResource(dataProduct);
    }

    @Override
    public DataProductResource deleteDataProduct(String id) {
        DataProduct dataProduct = dataProductService.deleteDataProduct(id);
        return dataProductMapper.toResource(dataProduct);
    }
}
