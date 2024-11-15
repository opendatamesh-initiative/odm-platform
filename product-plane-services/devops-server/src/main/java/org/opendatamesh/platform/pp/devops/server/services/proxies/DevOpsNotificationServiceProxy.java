package org.opendatamesh.platform.pp.devops.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.events.DataProductActivityEventState;
import org.opendatamesh.platform.pp.devops.api.resources.events.DataProductTaskEventState;
import org.opendatamesh.platform.pp.notification.api.clients.DispatchClient;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DevOpsNotificationServiceProxy {

    @Autowired(required = false)
    DispatchClient notificationClient;

    @Value("${odm.productPlane.notificationService.active}")
    private Boolean notificationServiceActive;

    private final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    // ======================================================================================
    // Activity Events
    // ======================================================================================

    public void notifyActivityCreation(ActivityResource activity, DataProductVersionDPDS dataProductVersion) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            JsonNode afterState = mapper.valueToTree(new DataProductActivityEventState(
                    activity,
                    dataProductVersion
            ));
            fixDpdsVersionFieldName(afterState);
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_ACTIVITY_CREATED,
                    activity.getId().toString(),
                    null,
                    afterState
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyActivityStart(ActivityResource activity, DataProductVersionDPDS dataProductVersion) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            JsonNode afterState = mapper.valueToTree(new DataProductActivityEventState(
                    activity,
                    dataProductVersion
            ));
            fixDpdsVersionFieldName(afterState);
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_ACTIVITY_STARTED,
                    activity.getId().toString(),
                    null,
                    afterState
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyActivityCompletion(ActivityResource activity, DataProductVersionDPDS dataProductVersion) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            JsonNode afterState = mapper.valueToTree(new DataProductActivityEventState(
                    activity,
                    dataProductVersion
            ));
            fixDpdsVersionFieldName(afterState);
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_ACTIVITY_COMPLETED,
                    activity.getId().toString(),
                    null, //TODO DATA_PRODUCT_ACTIVITY_COMPLETED must have beforeState!!!
                    afterState
            );
            notifyEvent(eventResource);
        }
    }


    // ======================================================================================
    // Task Events
    // ======================================================================================

    public void notifyTaskCreation(TaskResource task) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            JsonNode afterState = mapper.valueToTree(new DataProductTaskEventState(
                    task
            ));
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_TASK_CREATED,
                    task.getId().toString(),
                    null,
                    afterState
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyTaskStart(TaskResource task) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            JsonNode afterState = mapper.valueToTree(new DataProductTaskEventState(
                    task
            ));
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_TASK_STARTED,
                    task.getId().toString(),
                    null,
                    afterState
            );
            notifyEvent(eventResource);
        }
    }

    public void notifyTaskCompletion(TaskResource task) {
        if (Boolean.TRUE.equals(notificationServiceActive)) {
            JsonNode afterState = mapper.valueToTree(new DataProductTaskEventState(
                    task
            ));
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_TASK_CREATED,
                    task.getId().toString(),
                    null, //TODO DATA_PRODUCT_TASK_CREATED must have beforeState!!!
                    afterState
            );
            notifyEvent(eventResource);
        }
    }


    // ======================================================================================
    // Dispatch events
    // ======================================================================================


    private void notifyEvent(EventResource eventResource) {
        try {
            notificationClient.notifyEvent(eventResource);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void fixDpdsVersionFieldName(JsonNode eventStateTree) {
        //TODO this must be fixed on the dpds model!!!!
        if (eventStateTree != null && eventStateTree.has("dataProductVersion") && eventStateTree.get("dataProductVersion").has("info") && eventStateTree.get("dataProductVersion").get("info").has("versionNumber")) {
            JsonNode versionNumberNode = eventStateTree.get("").get("info").get("versionNumber");
            ((ObjectNode) versionNumberNode.get("dataProductVersion").get("info")).remove("versionNumber");
            ((ObjectNode) versionNumberNode.get("dataProductVersion").get("info")).set("version", versionNumberNode);
        }
    }
}
