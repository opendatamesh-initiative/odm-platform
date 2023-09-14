package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractDataProductVersionComponentsController;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataProductVersionComponentsController extends AbstractDataProductVersionComponentsController {

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(DataProductVersionComponentsController.class);

    public DataProductVersionComponentsController() {
        logger.debug("Data product version components controller succesfully started");
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
}
