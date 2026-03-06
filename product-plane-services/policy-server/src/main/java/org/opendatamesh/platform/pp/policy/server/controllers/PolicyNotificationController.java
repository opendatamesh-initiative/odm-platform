package org.opendatamesh.platform.pp.policy.server.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.notification.api.resources.v1.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.v1.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.v1.enums.EventType;
import org.opendatamesh.platform.pp.notification.api.resources.v2.EventV2NotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.v2.EventV2Resource;
import org.opendatamesh.platform.pp.notification.api.resources.v2.enums.EventV2EventType;
import org.opendatamesh.dpds.parser.IdentifierStrategy;
import org.opendatamesh.platform.pp.notification.api.resources.v2.events.DataProductDeletedEventContent;
import org.opendatamesh.platform.pp.notification.api.resources.v2.events.DataProductVersionDeletedEventContent;
import org.opendatamesh.platform.pp.policy.server.services.PolicyEvaluationResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PolicyNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyNotificationController.class);

    @Autowired
    private PolicyEvaluationResultService policyEvaluationResultService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IdentifierStrategy identifierStrategy;

    @PostMapping("/v1/up/observer/notifications")
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

    @PostMapping("/v2/up/observer/notifications")
    public ResponseEntity<Void> handleEventNotificationV2(@RequestBody EventV2NotificationResource eventNotification) {
        EventV2Resource event = eventNotification.getEvent();
        if (event == null) {
            logger.debug("Ignoring V2 notification with null event");
            return ResponseEntity.ok().build();
        }
        EventV2EventType eventType = event.getType();
        String resourceId = event.getResourceIdentifier();

        logger.info("Received V2 notification event: {} for resource: {}", eventType, resourceId);

        if (eventType == EventV2EventType.DATA_PRODUCT_DELETED) {
            handleDataProductDeletionV2(event);
        } else if (eventType == EventV2EventType.DATA_PRODUCT_VERSION_DELETED) {
            handleDataProductVersionDeletionV2(event);
        } else {
            logger.debug("Ignoring V2 event type: {}", eventType);
        }

        return ResponseEntity.ok().build();
    }

    private void handleDataProductDeletion(EventResource event) {
        String dataProductId = event.getEntityId();
        logger.info("Handling data product deletion for ID: {}", dataProductId);

        try {
            // Delete all policy evaluation results for this data product
            policyEvaluationResultService.deleteByDataProductId(dataProductId);
            logger.info("Successfully deleted policy evaluation results for data product: {}", dataProductId);
        } catch (Exception e) {
            logger.error("Failed to delete policy evaluation results for data product: {}", dataProductId, e);
        }
    }

    private void handleDataProductVersionDeletion(EventResource event) {
        String dataProductId = extractDataProductId(event);
        String version = extractVersion(event);

        logger.info("Handling data product version deletion for ID: {} version: {}", dataProductId, version);

        try {
            // Delete policy evaluation results for this specific data product version
            policyEvaluationResultService.deleteByDataProductIdAndVersion(dataProductId, version);
            logger.info("Successfully deleted policy evaluation results for data product: {} version: {}",
                    dataProductId, version);
        } catch (Exception e) {
            logger.error("Failed to delete policy evaluation results for data product: {} version: {}",
                    dataProductId, version, e);
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

    private void handleDataProductDeletionV2(EventV2Resource event) {
        if (event.getEventContent() == null) {
            logger.warn("Missing eventContent in V2 DATA_PRODUCT_DELETED event");
            return;
        }
        DataProductDeletedEventContent content;
        try {
            content = objectMapper.treeToValue(
                    event.getEventContent(), DataProductDeletedEventContent.class);
        } catch (Exception e) {
            logger.warn("Could not deserialize V2 DATA_PRODUCT_DELETED eventContent: {}", e.getMessage());
            return;
        }
        if (content == null || !StringUtils.hasText(content.getDataProductFqn())) {
            logger.warn("No dataProductFqn in V2 DATA_PRODUCT_DELETED event");
            return;
        }
        String dataProductId = identifierStrategy.getId(content.getDataProductFqn());
        logger.info("Handling V2 data product deletion for ID: {}", dataProductId);
        try {
            policyEvaluationResultService.deleteByDataProductId(dataProductId);
            logger.info("Successfully deleted policy evaluation results for data product: {}", dataProductId);
        } catch (Exception e) {
            logger.error("Failed to delete policy evaluation results for data product: {}", dataProductId, e);
        }
    }

    private void handleDataProductVersionDeletionV2(EventV2Resource event) {
        if (event.getEventContent() == null) {
            logger.warn("Missing eventContent in V2 DATA_PRODUCT_VERSION_DELETED event");
            return;
        }
        DataProductVersionDeletedEventContent content;
        try {
            content = objectMapper.treeToValue(
                    event.getEventContent(), DataProductVersionDeletedEventContent.class);
        } catch (Exception e) {
            logger.warn("Could not deserialize V2 DATA_PRODUCT_VERSION_DELETED eventContent: {}", e.getMessage());
            return;
        }
        if (content == null || !StringUtils.hasText(content.getDataProductFqn())) {
            logger.warn("No dataProductFqn in V2 DATA_PRODUCT_VERSION_DELETED event");
            return;
        }
        if (!StringUtils.hasText(content.getDataProductVersionNumber())) {
            logger.warn("No dataProductVersionNumber in V2 DATA_PRODUCT_VERSION_DELETED event");
            return;
        }
        String dataProductId = identifierStrategy.getId(content.getDataProductFqn());
        String version = content.getDataProductVersionNumber();
        logger.info("Handling V2 data product version deletion for ID: {} version: {}", dataProductId, version);
        try {
            policyEvaluationResultService.deleteByDataProductIdAndVersion(dataProductId, version);
            logger.info("Successfully deleted policy evaluation results for data product: {} version: {}",
                    dataProductId, version);
        } catch (Exception e) {
            logger.error("Failed to delete policy evaluation results for data product: {} version: {}",
                    dataProductId, version, e);
        }
    }
}
