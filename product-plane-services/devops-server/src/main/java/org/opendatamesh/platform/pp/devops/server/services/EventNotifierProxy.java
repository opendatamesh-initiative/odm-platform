package org.opendatamesh.platform.pp.devops.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.event.notifier.api.clients.EventNotifierClient;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventNotifierProxy {

    @Autowired
    EventNotifierClient eventNotifierClient;

    ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    // ======================================================================================
    // Activity Events
    // ======================================================================================

    public void notifyActivityCreation(ActivityResource activity) {
        EventResource eventResource = buildEvent(
                EventType.DATA_PRODUCT_ACTIVITY_CREATED,
                activity.getId().toString(),
                null,
                activity
        );
        notifyEvent(eventResource);
    }

    public void notifyActivityStart(ActivityResource activity) {
        EventResource eventResource = buildEvent(
                EventType.DATA_PRODUCT_ACTIVITY_STARTED,
                activity.getId().toString(),
                null,
                activity
        );
        notifyEvent(eventResource);
    }

    public void notifyActivityCompletion(ActivityResource activity) {
        EventResource eventResource = buildEvent(
                EventType.DATA_PRODUCT_ACTIVITY_COMPLETED,
                activity.getId().toString(),
                null,
                activity
        );
        notifyEvent(eventResource);
    }


    // ======================================================================================
    // Task Events
    // ======================================================================================

    public void notifyTaskCreation(TaskResource task) {
        EventResource eventResource = buildEvent(
                EventType.DATA_PRODUCT_TASK_CREATED,
                task.getId().toString(),
                null,
                task
        );
        notifyEvent(eventResource);
    }

    public void notifyTaskStart(TaskResource task) {
        EventResource eventResource = buildEvent(
                EventType.DATA_PRODUCT_TASK_STARTED,
                task.getId().toString(),
                null,
                task
        );
        notifyEvent(eventResource);
    }

    public void notifyTaskCompletion(TaskResource task) {
        EventResource eventResource = buildEvent(
                EventType.DATA_PRODUCT_TASK_COMPLETED,
                task.getId().toString(),
                null,
                task
        );
        notifyEvent(eventResource);
    }


    // ======================================================================================
    // Dispatch events
    // ======================================================================================

    public void notifyEvent(EventResource eventResource) {
        try {
            eventNotifierClient.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    "Impossible to upload activity to notificationService: " + t.getMessage(),
                    t
            );
        }
    }


    // ======================================================================================
    // Event creation
    // ======================================================================================

    public EventResource buildEvent(EventType eventType, String eventSubjectId, Object beforeState, Object afterState) {
        try {
            return new EventResource(
                    eventType,
                    eventSubjectId,
                    beforeState == null ? null : mapper.writeValueAsString(beforeState),
                    afterState == null ? null : mapper.writeValueAsString(afterState)
            );
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing Task as JSON: " + e.getMessage()
            );
        }
    }
}
