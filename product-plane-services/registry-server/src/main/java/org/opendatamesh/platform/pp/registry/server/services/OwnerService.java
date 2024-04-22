package org.opendatamesh.platform.pp.registry.server.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info.Owner;
import org.opendatamesh.platform.pp.registry.server.database.mappers.OwnerMapper;
import org.opendatamesh.platform.pp.registry.server.database.repositories.DataProductVersionRepository;
import org.opendatamesh.platform.pp.registry.server.database.repositories.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private DataProductVersionRepository dataProductVersionRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    NotificationClient eventNotifier;


    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    public OwnerService() { }

    // ======================================================================================
    // CREATE
    // ======================================================================================
    
    /**
     * 
     * @param owner
     * @return
     * 
     * @throws UnprocessableEntityException 
     *      SC422_17_OWNER_IS_INVALID
     *      SC422_18_OWNER_ALREADY_EXISTS
     * @throws InternalServerException 
     *      SC500_SERVICE_ERROR
     *      SC500_DATABASE_ERROR
     */
    public Owner createOwner(Owner owner) {
        if(owner == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Owner object cannot be null");
        }


        if(!StringUtils.hasText(owner.getId())) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_17_OWNER_IS_INVALID,
                "Owner Id property cannot be empty");
        }
        
        if(ownerExists(owner.getId())) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_18_OWNER_ALREADY_EXISTS,
                "Owner [" + owner.getId() + "] already exists");
        }

       
        try {
            owner = saveOwner(owner);
            logger.info("Owner [" + owner.getId() + "] successfully created");
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while saving owner [" + owner.getId() + "]",
                t);
        }
        return owner;
    }

    private Owner saveOwner(Owner owner) {
        return ownerRepository.saveAndFlush(owner);
    }

    // ======================================================================================
    // READ
    // ======================================================================================
    
    public List<Owner> readAllOwners() {
        List<Owner> owners = null;
        try {
            owners = loadAllOwner();
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while loading owners",
                t);
        }
        return owners;
    }

    private List<Owner> loadAllOwner() {
        return ownerRepository.findAll();
    }


    // -------------------------
    // exists methods
    // -------------------------


    public boolean ownerExists(String ownerId)  {
        return ownerRepository.existsById(ownerId);
    }
    
    
    // -------------------------
    // search methods
    // -------------------------

    public Owner searchOwner(String id) {
        Optional<Owner> ownerSearchResults = ownerRepository.findById(id);
        if(ownerSearchResults.isEmpty()) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_07_OWNER_NOT_FOUND,
                    "Owner with id [" + id + "] doesn't exists");
        }

        return ownerSearchResults.get();
    }


    // ======================================================================================
    // UPDATE
    // ======================================================================================
    
    public Owner updateOwner(Owner owner) {

        if(owner == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Owner cannot be null");
        }

        if(!StringUtils.hasText(owner.getId())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_17_OWNER_IS_INVALID,
                    "Owner Id property cannot be empty");
        }

        Owner oldOwner = searchOwner(owner.getId());
        if(oldOwner == null) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_07_OWNER_NOT_FOUND,
                    "Owner [" + owner.getId() + "] doesn't exists");
        }

        try {
            owner = saveOwner(owner);
            logger.info("Owner [" + owner.getId() + "] successfully updated");
        } catch(Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while updating owner [" + owner.getId() + "]",
                t);
        }

        return owner;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================
    
    public void deleteOwner(String ownerId)  {
        Owner owner = searchOwner(ownerId); //search if present

        List<DataProductVersion> dataProductVersions = dataProductVersionRepository.findByInfo_Owner_Id(owner.getId());

        if(dataProductVersions.isEmpty()) {
            try {

                ownerRepository.deleteById(ownerId);
                logger.info("Owner with id [" + ownerId + "] successfully deleted");

            } catch (Throwable t) {
                throw new InternalServerException(
                        ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                        "An error occurred in the backend database while deleting owner",
                        t);
            }
        }
        else {
            throw new ConflictException(
                    RegistryApiStandardErrors.SC409_03_OWNER_CAN_NOT_BE_DELETED,
                    "Owner [" + owner.getId() + "] has at least one Data Product associated, therefore can't be deleted");
        }
    }
}
