package org.opendatamesh.platform.pp.registry.server.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.*;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.Domain;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DomainMapper;
import org.opendatamesh.platform.pp.registry.server.database.repositories.DomainRepository;
import org.opendatamesh.platform.pp.registry.server.resources.v1.observers.EventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DomainService {

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DomainMapper domainMapper;

    @Autowired
    EventNotifier eventNotifier;


    private static final Logger logger = LoggerFactory.getLogger(DomainService.class);

    public DomainService() { }

    // ======================================================================================
    // CREATE
    // ======================================================================================
    
    /**
     * 
     * @param domain
     * @return
     * 
     * @throws UnprocessableEntityException 
     *      SC422_15_DOMAIN_IS_INVALID
     *      SC422_16_DOMAIN_ALREADY_EXISTS
     * @throws InternalServerException 
     *      SC500_SERVICE_ERROR
     *      SC500_DATABASE_ERROR
     */
    public Domain createDomain(Domain domain) {
        if(domain == null) {
            throw new InternalServerException(
                ODMRegistryAPIStandardError.SC500_00_SERVICE_ERROR,
                "Domain object cannot be null");
        }


        if(!StringUtils.hasText(domain.getFullyQualifiedName())) {
            throw new UnprocessableEntityException(
                ODMRegistryAPIStandardError.SC422_15_DOMAIN_IS_INVALID,
                "Domain fullyQualifiedName property cannot be empty");
        }

        String uuid = UUID.nameUUIDFromBytes(domain.getFullyQualifiedName().getBytes()).toString();
        if(domain.getId() != null && !domain.getId().equals(uuid)) {
            throw new UnprocessableEntityException(
                    ODMRegistryAPIStandardError.SC422_15_DOMAIN_IS_INVALID,
                    "Domain [" + domain.getFullyQualifiedName() + "] id [" + domain.getId()+ "] is invalid. Expected [" + uuid + "]");
        }
        domain.setId(uuid);
        
        if(domainExists(domain.getId())) {
            throw new UnprocessableEntityException(
                ODMRegistryAPIStandardError.SC422_16_DOMAIN_ALREADY_EXISTS,
                "Domain [" + domain.getFullyQualifiedName() + "] already exists");
        }

       
        try {
            domain = saveDomain(domain);
            logger.info("Domain [" + domain.getFullyQualifiedName() + "] successfully created");
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMRegistryAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while saving domain [" + domain.getFullyQualifiedName() + "]",
                t);
        }
        return domain;
    }

    private Domain saveDomain(Domain domain) {
        return domainRepository.saveAndFlush(domain);
    }

    // ======================================================================================
    // READ
    // ======================================================================================
    
    public List<Domain> readAllDomains() {
        List<Domain> domains = null;
        try {
            domains = loadAllDomains();
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMRegistryAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while loading domains",
                t);
        }
        return domains;
    }

    private List<Domain> loadAllDomains() {
        return domainRepository.findAll();
    }


    // -------------------------
    // exists methods
    // -------------------------


    public boolean domainExists(String domainId)  {
        return domainRepository.existsById(domainId);
    }
    
    
    // -------------------------
    // search methods
    // -------------------------

    public Domain searchDomain(String id) {
        Optional<Domain> domainSearchResults = domainRepository.findById(id);
        if(domainSearchResults.isEmpty()) {
            throw new NotFoundException(
                    ODMRegistryAPIStandardError.SC404_06_DOMAIN_NOT_FOUND,
                    "Domain with id [" + id + "] doesn't exists");
        }

        return domainSearchResults.get();
    }

    public Domain searchDomainByFQN(String fqn) {
        Optional<Domain> domainSearchResults = domainRepository.findByFullyQualifiedName(fqn);
        if(domainSearchResults.isEmpty()) {
            throw new NotFoundException(
                    ODMRegistryAPIStandardError.SC404_06_DOMAIN_NOT_FOUND,
                    "Domain with fqn [" + fqn + "] doesn't exists");
        }

        return domainSearchResults.get();
    }


    // ======================================================================================
    // UPDATE
    // ======================================================================================
    
    public Domain updateDomain(Domain domain) {

        if(domain == null) {
            throw new InternalServerException(
                    ODMRegistryAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Domain cannot be null");
        }

        if(!StringUtils.hasText(domain.getFullyQualifiedName())) {
            throw new UnprocessableEntityException(
                    ODMRegistryAPIStandardError.SC422_15_DOMAIN_IS_INVALID,
                    "Domain fullyQualifiedName property cannot be empty");
        }

        Domain oldDomain = searchDomainByFQN(domain.getFullyQualifiedName());
        if(oldDomain == null) {
            throw new NotFoundException(
                    ODMRegistryAPIStandardError.SC404_06_DOMAIN_NOT_FOUND,
                    "Domain [" + oldDomain.getFullyQualifiedName() + "] doesn't exists");
        }
        domain.setId(oldDomain.getId());

        try {
            domain = saveDomain(domain);
            logger.info("Domain [" + domain.getFullyQualifiedName() + "] succesfully updated");
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMRegistryAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while updating domain [" + domain.getFullyQualifiedName() + "]",
                t);
        }

        return domain;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================
    
    public void deleteDomain(String domainId)  {
        Domain domain = searchDomain(domainId);
        // TODO decide whether delete all the data product with this domain
        try {
            domainRepository.deleteById(domain.getId());
            logger.info("Domain with id [" + domainId + "] successfully deleted");
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMRegistryAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while deleting domain",
                t);
        }

    }
}
