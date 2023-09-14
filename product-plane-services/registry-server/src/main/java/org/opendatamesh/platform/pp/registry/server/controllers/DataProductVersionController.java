package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractDataProductVersionController;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.ApplicationComponent;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.InfrastructuralComponent;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DataProductVersionController extends AbstractDataProductVersionController
{

    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    ObjectMapper objectMapper;

    
    private static final Logger logger = LoggerFactory.getLogger(DataProductVersionController.class);

    public DataProductVersionController() { 
        logger.debug("Data product version controller succesfully started");
    }


    @Override
    public String createDataProductVersion(String id, String descriptorContent)  {

        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!StringUtils.hasText(descriptorContent)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_01_DESCRIPTOR_IS_EMPTY,
                "Input descriptor document cannot be empty");
        }

        UriLocation descriptorLocation = new UriLocation(descriptorContent);
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        DataProductVersion dataProductVersion = dataProductService.addDataProductVersion(id, descriptorLocation, serverUrl);
        DataProductVersionDPDS dataProductVersionDPDS = dataProductVersionMapper.toResource(dataProductVersion);
        
        String serailizedContent = null;
        try {
            serailizedContent = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(dataProductVersionDPDS, "canonical");
        } catch (JsonProcessingException e) {
           throw new InternalServerException(
            ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
            "Impossible to serialize data product version raw content", e);
        }
        return serailizedContent;
    }

   @Override
    public List<String> getDataProductVersions(String id) 
    {
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!dataProductService.dataProductExists(id)) {
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                "Data product not found");
        }

        List<String> versions = new ArrayList<>();
        List<DataProductVersion> dataProductVersions = dataProductVersionService.searchDataProductVersions(id);
        versions = dataProductVersions.stream().map(dpv -> dpv.getVersionNumber()).collect(Collectors.toList());
        return versions;
    }

    @Override
    public String getDataProductVersion(String productId, String version, String format) {
        if(!StringUtils.hasText(productId)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!StringUtils.hasText(version)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_11_PRODUCT_VERSION_NUMBER_IS_EMPTY,
                "Data product version number is empty");
        }
        
        
        if(StringUtils.hasText(format) && !(format.equalsIgnoreCase("normalized") || format.equalsIgnoreCase("canonical"))) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_04_INVALID_FORMAT,
                "Format [" + format + "] is not supported");
        }
        
        DataProductVersion dataProductVersion = dataProductVersionService.readDataProductVersion(productId, version);

        DataProductVersionDPDS dataProductVersionDPDS = dataProductVersionMapper.toResource(dataProductVersion);
        if(format == null) format = "canonical";
        DPDSSerializer serializer = new DPDSSerializer();
        String serailizedContent = null;
        try {
            serailizedContent = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(dataProductVersionDPDS, format);
        } catch (JsonProcessingException e) {
           throw new InternalServerException(
            ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
            "Impossible to serialize data product version raw content", e);
        }
        return serailizedContent;
    }

    @Override
    public void deleteDataProductVersion(String id, String version
    ) {
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!StringUtils.hasText(version)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_11_PRODUCT_VERSION_NUMBER_IS_EMPTY,
                "Data product version number is empty");
        }

        if(!dataProductService.dataProductExists(id)) {
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                "Data product not found");
        }

        dataProductVersionService.deleteDataProductVersion(id, version);
    }

    public String getDataProductVersionApplications(String id, String version, String format)
    {
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                    "Id cannot be cannot be empty");
        }
        if(!StringUtils.hasText(version)) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                    "Version cannot be cannot be empty");
        }

        if(!dataProductService.dataProductExists(id)) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                    "Data product not found");
        }

        String result;
        List<ApplicationComponent> applicationComponents = dataProductVersionService.searchDataProductVersionApplicationComponents(id, version);
        List<ApplicationComponentDPDS> applicationComponentsDPDS = dataProductVersionMapper.applicationComponentsToApplicationComponentResources(applicationComponents);

        if(format == null) format = "canonical";
        try {
            result = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(applicationComponentsDPDS, format);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    "Impossible to serialize component version raw content", e);
        }
        return result;
    }

    public String getDataProductVersionInfrastructures(String id, String version, String format){
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                    "Id cannot be cannot be empty");
        }
        if(!StringUtils.hasText(version)) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                    "Version cannot be cannot be empty");
        }

        if(!dataProductService.dataProductExists(id)) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                    "Data product not found");
        }

        String result;
        List<InfrastructuralComponent> infrastructuralComponents = dataProductVersionService.searchDataProductVersionInfrastructuralComponents(id, version);
        List<InfrastructuralComponentDPDS> infrastructuralComponentsDPDS = dataProductVersionMapper.infrastructuralComponentsToInfrastructuralComponentResources(infrastructuralComponents);

        if(format == null) format = "canonical";
        try {
            result = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(infrastructuralComponentsDPDS, format);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    "Impossible to serialize component version raw content", e);
        }
        return result;
    }

}
