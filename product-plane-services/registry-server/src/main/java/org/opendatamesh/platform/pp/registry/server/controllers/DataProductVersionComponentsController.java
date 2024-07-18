package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.dpds.model.internals.InfrastructuralComponentDPDS;
import org.opendatamesh.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractDataProductVersionComponentsController;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.api.resources.VariableResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.ApplicationComponent;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.InfrastructuralComponent;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.database.mappers.VariableMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataProductVersionComponentsController extends AbstractDataProductVersionComponentsController {

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    private VariableMapper variableMapper;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(DataProductVersionComponentsController.class);

    public DataProductVersionComponentsController() {
        logger.debug("Data product version components controller successfully started");
    }

    @Override
    public String getPorts(
            String id, String version, String portType, String format) {

        EntityTypeDPDS entityType;
        if (portType == null) {
            entityType = null;
        } else {
            entityType = EntityTypeDPDS.resolvePropertyValue(portType);
            if (entityType == null || !entityType.isPort()) {
                throw new BadRequestException(
                        RegistryApiStandardErrors.SC400_06_INVALID_PORTTYPE,
                        "Value [" + portType + "] is not a valid port type");
            }
        }

        if (format == null) {
            format = "normalized";
        } else {
            if (!format.equals("normalized") || !format.equals("canonical")) {
                throw new BadRequestException(
                        RegistryApiStandardErrors.SC400_04_INVALID_FORMAT,
                        "Value [" + format + "] is not a valid format");
            }
        }

        DataProductVersion dataProductVersion = dataProductVersionService.readDataProductVersion(id, version);
        DataProductVersionDPDS dataProductVersionDPDS = dataProductVersionMapper.toResource(dataProductVersion);

        String result = null;

        if("canonical".equals(format)){
           
                DPDSSerializer deserializer = DPDSSerializer.DEFAULT_JSON_SERIALIZER;
                try {
                    result =  deserializer.serializeToCanonicalForm(dataProductVersionDPDS.getInterfaceComponents(),
                            entityType);
                } catch (Throwable t) {
                    throw new InternalServerException(
                        ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                        "Impossible to serialize ports to format  [" + format + "] is not supported", t);
                }
         } else if("normalized".equals(format)){
                try {
                    result = objectMapper.writeValueAsString(dataProductVersionDPDS.getInterfaceComponents());
                } catch (Throwable t) {
                    throw new InternalServerException(
                        ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                        "Impossible to serialize ports to format  [" + format + "] is not supported", t);
                }
        } else {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_04_INVALID_FORMAT,
                "Format [" + format + "] is not supported");
        }

        return result;
    }

    @Override
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

    @Override
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

    @Override
    public List<VariableResource> getVariables(String id, String version) {
        return variableMapper.toResources(
                dataProductVersionService.readDataProductVersionVariables(id, version)
        );
    }

    @Override
    public VariableResource updateVariable(String id, String version, Long variableId, String variableValue) {
        return variableMapper.toResource(
                dataProductVersionService.updateDataProductVersionVariable(id, version, variableId, variableValue)
        );
    }

}
