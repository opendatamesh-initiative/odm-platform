package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ReferenceObject;


@Mapper(componentModel = "spring")
public interface ReferenceObjectMapper { 
    
    ReferenceObject toEntity(ReferenceObjectDPDS resource);
    ReferenceObjectDPDS toResource(ReferenceObject entity);
}
