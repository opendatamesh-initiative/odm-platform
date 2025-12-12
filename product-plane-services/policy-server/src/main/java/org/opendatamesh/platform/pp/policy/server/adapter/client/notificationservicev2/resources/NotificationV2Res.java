package org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NotificationV2Res {
    private Long sequenceId;
    private NotificationV2StatusRes status;
    private NotificationV2EventRes event;
    private String errorMessage;
    private ObjectNode subscription;

    public NotificationV2Res() {
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public NotificationV2StatusRes getStatus() {
        return status;
    }

    public void setStatus(NotificationV2StatusRes status) {
        this.status = status;
    }

    public NotificationV2EventRes getEvent() {
        return event;
    }

    public void setEvent(NotificationV2EventRes event) {
        this.event = event;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public enum NotificationV2StatusRes {
        PROCESSING,
        PROCESSED,
        FAILED_TO_DELIVER,
        FAILED_TO_PROCESS
    }

    public ObjectNode getSubscription() {
        return subscription;
    }

    public void setSubscription(ObjectNode subscription) {
        this.subscription = subscription;
    }

    public static class NotificationV2EventRes {
        private Long sequenceId;
        private String resourceType;
        private String resourceIdentifier;
        private String type;
        private String eventTypeVersion;
        private JsonNode eventContent;

        public NotificationV2EventRes() {
        }

        public Long getSequenceId() {
            return sequenceId;
        }

        public void setSequenceId(Long sequenceId) {
            this.sequenceId = sequenceId;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getResourceIdentifier() {
            return resourceIdentifier;
        }

        public void setResourceIdentifier(String resourceIdentifier) {
            this.resourceIdentifier = resourceIdentifier;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEventTypeVersion() {
            return eventTypeVersion;
        }

        public void setEventTypeVersion(String eventTypeVersion) {
            this.eventTypeVersion = eventTypeVersion;
        }

        public JsonNode getEventContent() {
            return eventContent;
        }

        public void setEventContent(JsonNode eventContent) {
            this.eventContent = eventContent;
        }
    }

}