package org.opendatamesh.platform.pp.devops.server.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/up/observer")
public class DevOpsNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(DevOpsNotificationController.class);

    @Autowired
    private ActivityService activityService;

    @PostMapping("/notifications")
    public ResponseEntity<Void> handleEventNotification(@RequestBody EventNotificationResource eventNotification) {
        EventResource event = eventNotification.getEvent();
        String eventType = event.getType();

        logger.info("Received notification event: {} for entity: {}", eventType, event.getEntityId());

        if (EventType.DATA_PRODUCT_DELETED.name().equals(eventType)) {
            handleDataProductDeletion(event);
        } else if (EventType.DATA_PRODUCT_VERSION_DELETED.name().equals(eventType)) {
            handleDataProductVersionDeletion(event);
        } else {
            logger.debug("Ignoring event type: {}", eventType);
        }

        return ResponseEntity.ok().build();

    }

    private void handleDataProductDeletion(EventResource event) {
        String dataProductId = event.getEntityId();
        logger.info("Handling data product deletion for ID: {}", dataProductId);

        try {
            // Delete all activities and tasks for this data product
            activityService.deleteByDataProductId(dataProductId);
            logger.info("Successfully deleted activities and tasks for data product: {}", dataProductId);
        } catch (Exception e) {
            logger.error("Failed to delete activities and tasks for data product: {}", dataProductId, e);
            throw e;
        }
    }

    private void handleDataProductVersionDeletion(EventResource event) {
        String dataProductId = extractDataProductId(event);
        String version = extractVersion(event);

        logger.info("Handling data product version deletion for ID: {} version: {}", dataProductId, version);

        try {
            // Delete activities and tasks for this specific data product version
            activityService.deleteByDataProductIdAndVersion(dataProductId, version);
            logger.info("Successfully deleted activities and tasks for data product: {} version: {}",
                    dataProductId, version);
        } catch (Exception e) {
            logger.error("Failed to delete activities and tasks for data product: {} version: {}",
                    dataProductId, version, e);
            throw e;
        }
    }

    private String extractDataProductId(EventResource event) {
        // For data product version deletion, the entityId contains the data product ID
        // as set in RegistryNotificationServiceProxy.buildDataProductVersionEvent()
        return event.getEntityId();
    }

    private String extractVersion(EventResource event) {
        // Extract version from the beforeState which contains the deleted data product
        // version
        JsonNode beforeState = event.getBeforeState();
        if (beforeState != null && beforeState.has("dataProductVersion")) {
            JsonNode versionNode = beforeState.get("dataProductVersion");
            if (versionNode.has("info") && versionNode.get("info").has("version")) {
                return versionNode.get("info").get("version").asText();
            }
        }

        logger.warn("Could not extract version from event beforeState, entityId: {}", event.getEntityId());
        throw new IllegalArgumentException("Could not extract version from data product version deletion event");
    }
}
