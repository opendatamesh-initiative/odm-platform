package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.*;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.*;

@Mapper(componentModel = "spring")
public interface DataProductMapper { 

    DataProduct toEntity(DataProductResource resource);
    DataProductResource toResource(DataProduct entity);

    DataProductVersion toEntity(DataProductVersionResource resource);
    DataProductVersionResource toResource(DataProductVersion entity);

    List<DataProductVersionResource> toResources(List<DataProductVersion> entities);
    List<DataProductResource> dataProductsToResources(List<DataProduct> entities);


    InfoResource infoToInfoResource(Info entity);
   
    
    Info infoResourceToInfo(InfoResource entity);
       
   
    Port portResourceToPort(PortResource portResource);

    
    ApplicationComponent applicationComponentResourceToApplicationComponent(ApplicationComponentResource applicationComponentResource);

    
    InfrastructuralComponent infrastructuralComponentResourceToInfrastructuralComponent(InfrastructuralComponentResource infrastructuralComponentResource);
}
