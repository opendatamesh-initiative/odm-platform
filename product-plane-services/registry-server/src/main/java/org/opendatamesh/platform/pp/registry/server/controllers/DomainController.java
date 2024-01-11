package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractDomainController;
import org.opendatamesh.platform.pp.registry.api.resources.DomainResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.Domain;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DomainMapper;
import org.opendatamesh.platform.pp.registry.server.services.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DomainController extends AbstractDomainController
{
    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainMapper domainMapper;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(DomainController.class);

    public DomainController() {
        logger.debug("Domain controller successfully started");
    }

  
    // ======================================================================================
    // DOMAINS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE Domain
    // ----------------------------------------
   
    @Override
    public DomainResource createDomain(DomainResource domainRes) throws Exception {
        if(domainRes == null) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_16_DOMAIN_IS_EMPTY,
                "Domain cannot be empty");
        }

        Domain domain = domainMapper.toEntity(domainRes);
        domain = domainService.createDomain(domain);
        return domainMapper.toResource(domain);
    }

    // ----------------------------------------
    // READ All Domains
    // ----------------------------------------


    @Override
    public List<DomainResource> getAllDomains() {
        return domainMapper.domainToResources(domainService.readAllDomains());
    }

    // ----------------------------------------
    // READ Domain
    // ----------------------------------------

    @Override
    public DomainResource getDomain(String id) {
        Domain domain = domainService.searchDomain(id);
        DomainResource domainResource = domainMapper.toResource(domain);
        return domainResource;
    }

    // ----------------------------------------
    // DELETE Domain
    // ----------------------------------------

    @Override
    public void deleteDomain( String id) {
        domainService.deleteDomain(id);
    }

    // ----------------------------------------
    // UPDATE Domain
    // ----------------------------------------

    @Override
    public DomainResource updateDomain( DomainResource domainRes) throws Exception {

        if(domainRes == null)
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_16_DOMAIN_IS_EMPTY,
                "Domain is empty"
            );

        Domain domain = domainMapper.toEntity(domainRes);
        domain = domainService.updateDomain(domain);

        return domainMapper.toResource(domain);
    }
}
