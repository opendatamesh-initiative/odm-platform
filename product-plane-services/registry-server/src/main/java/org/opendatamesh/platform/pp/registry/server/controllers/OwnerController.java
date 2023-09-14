package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractOwnerController;
import org.opendatamesh.platform.pp.registry.api.resources.OwnerResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info.Owner;
import org.opendatamesh.platform.pp.registry.server.database.mappers.OwnerMapper;
import org.opendatamesh.platform.pp.registry.server.services.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OwnerController extends AbstractOwnerController
{
    @Autowired
    private OwnerService ownerService;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

    public OwnerController() {
        logger.debug("Owner controller successfully started");
    }

  
    // ======================================================================================
    // OWNERS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE Owner
    // ----------------------------------------
   
    @Override
    public OwnerResource createOwner(OwnerResource ownerResource) throws Exception {
        if(ownerResource == null) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_17_OWNER_IS_EMPTY,
                "Owner cannot be empty");
        }

        Owner owner = ownerMapper.toEntity(ownerResource);
        owner = ownerService.createOwner(owner);
        return ownerMapper.toResource(owner);
    }

    // ----------------------------------------
    // READ All Owners
    // ----------------------------------------


    @Override
    public List<OwnerResource> getAllOwners() {
        return ownerMapper.ownersToResources(ownerService.readAllOwners());
    }

    // ----------------------------------------
    // READ Owner
    // ----------------------------------------

    @Override
    public OwnerResource getOwner(String id) {
        Owner owner = ownerService.searchOwner(id);
        OwnerResource ownerResource = ownerMapper.toResource(owner);
        return ownerResource;
    }

    // ----------------------------------------
    // DELETE Owner
    // ----------------------------------------

    @Override
    public void deleteOwner(String id) {
        ownerService.deleteOwner(id);
    }

    // ----------------------------------------
    // UPDATE Owner
    // ----------------------------------------

    @Override
    public OwnerResource updateOwner(OwnerResource ownerResource) throws Exception {

        if(ownerResource == null)
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_17_OWNER_IS_EMPTY,
                "Owner is empty"
            );

        Owner owner = ownerMapper.toEntity(ownerResource);
        owner = ownerService.updateOwner(owner);

        return ownerMapper.toResource(owner);
    }
}
