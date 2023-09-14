package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.OwnerResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info.Owner;

import java.util.List;


@Mapper(componentModel = "spring")
public interface OwnerMapper {

    Owner toEntity(OwnerResource resource);
    OwnerResource toResource(Owner entity);

    List<OwnerResource> ownersToResources(List<Owner> entities);
}
