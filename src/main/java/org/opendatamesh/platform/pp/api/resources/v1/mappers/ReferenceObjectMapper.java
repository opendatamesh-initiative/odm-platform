package org.opendatamesh.platform.pp.api.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.api.database.entities.dataproduct.*;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.*;

@Mapper(componentModel = "spring")
public interface ReferenceObjectMapper { 
    
    ReferenceObject toEntity(ReferenceObjectResource resource);
    ReferenceObjectResource toResource(ReferenceObject entity);
}
