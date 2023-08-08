package org.opendatamesh.platform.pp.devops.server.database.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    
    Task toEntity(TaskResource resource);
    TaskResource toResource(Task entity);
    List<TaskResource> toResources(List<Task> entities);  
}


