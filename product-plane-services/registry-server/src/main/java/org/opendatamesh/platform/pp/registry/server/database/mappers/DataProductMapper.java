package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DataProductMapper { 

    DataProduct toEntity(DataProductResource resource);
    DataProductResource toResource(DataProduct entity);

    List<DataProductResource> toResources(List<DataProduct> entities);
}
