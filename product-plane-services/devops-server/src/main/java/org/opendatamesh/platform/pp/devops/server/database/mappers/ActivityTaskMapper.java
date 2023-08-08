package org.opendatamesh.platform.pp.devops.server.database.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

@Mapper(componentModel = "spring")
public interface ActivityTaskMapper {
    
    Task toEntity(ActivityTaskResource resource);
    ActivityTaskResource toResource(Task entity);
    List<ActivityTaskResource> toResources(List<Task> entities);  
     
}


