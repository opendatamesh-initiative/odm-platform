package org.opendatamesh.platform.pp.policy.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.ActivityResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.ActivityStageTransitionEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.DataProductEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface EventTypeBaseMapper {

    @Mapping(target = "dataProductVersion", source = "dataProductVersionDPDS")
    DataProductEventTypeResource toEventResource(DataProductVersionDPDS dataProductVersionDPDS);

    @Mapping(target = "lifecycle", source = "lifecycleResource")
    @Mapping(target = "activity", source = "activityResource")
    @Mapping(target = "tasks", source = "taskResourceList")
    ActivityStageTransitionEventTypeResource toEventResource(
            LifecycleResource lifecycleResource,
            ActivityResource activityResource,
            List<TaskResource> taskResourceList
    );

    @Mapping(target = "activity", source = "activityResource")
    @Mapping(target = "task", source = "taskResource")
    TaskResultEventTypeResource toEventResource(
            ActivityResource activityResource,
            TaskResource taskResource
    );

    @Mapping(target = "activity", source = "activityResource")
    @Mapping(target = "dataProductVersion", source = "dataProductVersionDPDS")
    ActivityResultEventTypeResource toEventResource(
            ActivityResource activityResource,
            DataProductVersionDPDS dataProductVersionDPDS
    );

}
