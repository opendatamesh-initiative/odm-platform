package org.opendatamesh.platform.pp.devops.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    
    Task toEntity(TaskResource resource);
    TaskResource toResource(Task entity);
    List<TaskResource> toResources(List<Task> entities);  
}


