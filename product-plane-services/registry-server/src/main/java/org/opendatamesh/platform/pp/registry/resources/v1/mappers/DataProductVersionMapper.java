package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ApiDefinitionReference;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ApplicationComponent;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DefinitionReference;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ExternalResource;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.Info;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.InfrastructuralComponent;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.Port;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ReferenceObject;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.StandardDefinition;


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

    StandardDefinition toEntity(StandardDefinitionDPDS resource);
    StandardDefinitionDPDS toResource(StandardDefinition entity);

    ReferenceObject toEntity(ReferenceObjectDPDS resource);
    ReferenceObjectDPDS toResource(ReferenceObject entity);

    @SubclassMapping (target = ApiDefinitionReference.class, source = ApiDefinitionReferenceDPDS.class)
    DefinitionReference toEntity(DefinitionReferenceDPDS resource);
    DefinitionReferenceDPDS toResource(DefinitionReference entity);  
    
    ApiDefinitionReference toEntity(ApiDefinitionReferenceDPDS resource);
    ApiDefinitionReferenceDPDS toResource(ApiDefinitionReference entity);  

    ExternalResource toEntity(ExternalResourceDPDS resource);
    ExternalResourceDPDS toResource(ExternalResource entity);

    /* 
    @Mapping(source = "rawContent", target = "href", qualifiedByName = "rawContentToHref")
    ExternalResource toEntityTemplate(ReferenceObjectDPDS resource);

    @Named("rawContentToHref")
    static String rawContentToHref(String rawContent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (rawContent == null)
            return null;
        ExternalResourceDPDS externalResourceDPDS = objectMapper.readValue(rawContent, ExternalResourceDPDS.class);
        return externalResourceDPDS.getHref();
    }
    */
}
