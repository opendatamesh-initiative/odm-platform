package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ApplicationComponent;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProduct;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.Info;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.InfrastructuralComponent;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.Port;
import org.opendatamesh.platform.pp.registry.resources.v1.DataProductResource;


@Mapper(componentModel = "spring")
public interface DataProductMapper { 

    DataProduct toEntity(DataProductResource resource);
    DataProductResource toResource(DataProduct entity);

    DataProductVersion toEntity(DataProductVersionDPDS resource);
    DataProductVersionDPDS toResource(DataProductVersion entity);

    List<DataProductVersionDPDS> toResources(List<DataProductVersion> entities);
    List<DataProductResource> dataProductsToResources(List<DataProduct> entities);

    InfoDPDS infoToInfoResource(Info entity);
    Info infoResourceToInfo(InfoDPDS entity);
   
    Port portResourceToPort(PortDPDS portDPDS);    
    ApplicationComponent applicationComponentResourceToApplicationComponent(ApplicationComponentDPDS applicationComponentDPDS);
    InfrastructuralComponent infrastructuralComponentResourceToInfrastructuralComponent(InfrastructuralComponentDPDS infrastructuralComponentDPDS);
}
