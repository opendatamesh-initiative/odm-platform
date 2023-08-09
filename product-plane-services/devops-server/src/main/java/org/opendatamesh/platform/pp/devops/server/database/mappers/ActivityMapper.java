package org.opendatamesh.platform.pp.devops.server.database.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    Activity toEntity(ActivityResource resource);
    ActivityResource toResource(Activity entity);
    List<ActivityResource> toResources(List<Activity> entities);  
    
    Task toTaskEntity(TaskResource resource);
    TaskResource toTaskResource(Task entity);
    List<TaskResource> toTaskResources(List<Task> entities);  
     
}


