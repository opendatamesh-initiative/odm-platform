package org.opendatamesh.platform.pp.devops.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityTaskMapper {
    
    Task toEntity(ActivityTaskResource resource);
    ActivityTaskResource toResource(Task entity);
    List<ActivityTaskResource> toResources(List<Task> entities);  
     
}


