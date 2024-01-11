package org.opendatamesh.platform.pp.devops.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Lifecycle;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LifecycleMapper {

    Lifecycle toEntity(LifecycleResource resource);
    LifecycleResource toResource(Lifecycle entity);
    List<LifecycleResource> toResources(List<Lifecycle> entities);

}
