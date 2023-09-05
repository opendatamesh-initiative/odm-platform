package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.GitLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractUploadsController;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductDescriptorLocationResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

// TODO verify if is it possible to expose only one endpoints and using 

@RestController
public class UploadsController extends AbstractUploadsController {
    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;


    @Autowired
    ObjectMapper objectMapper;
    
    private static final Logger logger = LoggerFactory.getLogger(UploadsController.class);

    public UploadsController() { 
        logger.debug("Data product uploads controller succesfully started");
    }
   
    @Override
    public String uploadDataProductVersion(DataProductDescriptorLocationResource descriptorLocationRes) {
        DescriptorLocation descriptorLocation = null;
        try {
            URI descriptorUri = new URI(descriptorLocationRes.getRootDocumentUri());
            if(descriptorLocationRes.getGit() != null 
                && StringUtils.hasText(descriptorLocationRes.getGit().getRepositorySshUri())) {
                descriptorLocation = new GitLocation(
                    descriptorLocationRes.getGit().getRepositorySshUri(), descriptorUri,
                    descriptorLocationRes.getGit().getBranch(), descriptorLocationRes.getGit().getTag()
                );
            } else {
                descriptorLocation = new UriLocation(descriptorUri);
            }

        } catch (URISyntaxException e) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_05_INVALID_URILIST,
                "Provided URI is invalid [" + descriptorLocationRes.getRootDocumentUri() + "]", e);
        }
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        DataProductVersion dataProductVersion = dataProductService.addDataProductVersion(descriptorLocation, true, serverUrl);
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


    // ----------------
    // Private methods
    // ----------------

    // @see https://www.iana.org/assignments/media-types/text/uri-list
    private URI getUriFromUriListString(String uriListString) {
        
        String descriptorUriString = null;
        if(!StringUtils.hasText(uriListString) ) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_05_INVALID_URILIST,
                "Request body is empty");
        } 
        String[] uriList = uriListString.split("\r\n");
        for(String uri: uriList) {
            if(uri.trim().startsWith("#")) continue;
            else {descriptorUriString = uri.trim(); break;}
        }
        if(descriptorUriString == null) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_05_INVALID_URILIST,
                "Request body does not contain an URI");
        }
        
        URI descriptorUri = null;
        try {
            descriptorUri = new URI(uriListString);
        } catch (URISyntaxException e) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_05_INVALID_URILIST,
                "Provided URI is invalid [" + uriListString + "]", e);
        }

        return descriptorUri;
    }
}
