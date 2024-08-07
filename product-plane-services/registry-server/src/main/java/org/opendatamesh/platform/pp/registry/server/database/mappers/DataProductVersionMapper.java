package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.core.ExternalResourceDPDS;
import org.opendatamesh.dpds.model.core.ReferenceObjectDPDS;
import org.opendatamesh.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.dpds.model.internals.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.DefinitionReference;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ReferenceObject;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.definitions.ApiDefinitionReference;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info.Info;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces.Port;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.ApplicationComponent;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.InfrastructuralComponent;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DataProductVersionMapper {

    DataProductVersion toEntity(DataProductVersionDPDS resource);
    DataProductVersionDPDS toResource(DataProductVersion entity);

    List<DataProductVersionDPDS> toResources(List<DataProductVersion> entities);
    
    InfoDPDS infoToInfoResource(Info entity);
    Info infoResourceToInfo(InfoDPDS entity);
   
    Port portResourceToPort(PortDPDS portDPDS);
    ApplicationComponent applicationComponentResourceToApplicationComponent(ApplicationComponentDPDS applicationComponentDPDS);
    InfrastructuralComponent infrastructuralComponentResourceToInfrastructuralComponent(InfrastructuralComponentDPDS infrastructuralComponentDPDS);

    List<ApplicationComponentDPDS> applicationComponentsToApplicationComponentResources(List<ApplicationComponent> entities);
    List<InfrastructuralComponentDPDS> infrastructuralComponentsToInfrastructuralComponentResources(List<InfrastructuralComponent> entities);

    //StandardDefinition toEntity(StandardDefinitionDPDS resource);
    //StandardDefinitionDPDS toResource(StandardDefinition entity);

    ReferenceObject toEntity(ReferenceObjectDPDS resource);
    ReferenceObjectDPDS toResource(ReferenceObject entity);

    @SubclassMapping (target = ApiDefinitionReference.class, source = ApiDefinitionReferenceDPDS.class)
    DefinitionReference toEntity(DefinitionReferenceDPDS resource);
    DefinitionReferenceDPDS toResource(DefinitionReference entity);
    
    ApiDefinitionReference toEntity(ApiDefinitionReferenceDPDS resource);
    ApiDefinitionReferenceDPDS toResource(ApiDefinitionReference entity);  

    ExternalResource toEntity(ExternalResourceDPDS resource);
    ExternalResourceDPDS toResource(ExternalResource entity);
}
