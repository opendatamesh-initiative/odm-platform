package org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.received;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;

public class DataProductVersionPublicationRequestedEventRes {
    private final String resourceType = "DATA_PRODUCT";
    private String resourceIdentifier;
    private final String type = NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED.getValue();
    private final String eventTypeVersion = "v2.0.0";
    private EventContent eventContent;

    public DataProductVersionPublicationRequestedEventRes() {
    }

    public String getResourceType() {
        return resourceType;
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

    public String getEventTypeVersion() {
        return eventTypeVersion;
    }

    public EventContent getEventContent() {
        return eventContent;
    }

    public void setEventContent(EventContent eventContent) {
        this.eventContent = eventContent;
    }

    public static class EventContent {
        private DataProductVersionRes previousDataProductVersion;
        private DataProductVersionRes dataProductVersion;

        public EventContent() {
        }

        public DataProductVersionRes getPreviousDataProductVersion() {
            return previousDataProductVersion;
        }

        public void setPreviousDataProductVersion(DataProductVersionRes previousDataProductVersion) {
            this.previousDataProductVersion = previousDataProductVersion;
        }

        public DataProductVersionRes getDataProductVersion() {
            return dataProductVersion;
        }

        public void setDataProductVersion(DataProductVersionRes dataProductVersion) {
            this.dataProductVersion = dataProductVersion;
        }
    }


    public static class DataProductVersionRes {
        private String uuid;
        private DataProductRes dataProduct;
        private String name;
        private String description;
        private String tag;
        private String validationState;
        private String spec;
        private String specVersion;
        private JsonNode content;
        private String createdBy;
        private String updatedBy;

        public DataProductVersionRes() {
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public DataProductRes getDataProduct() {
            return dataProduct;
        }

        public void setDataProduct(DataProductRes dataProduct) {
            this.dataProduct = dataProduct;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getValidationState() {
            return validationState;
        }

        public void setValidationState(String validationState) {
            this.validationState = validationState;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public String getSpecVersion() {
            return specVersion;
        }

        public void setSpecVersion(String specVersion) {
            this.specVersion = specVersion;
        }

        public JsonNode getContent() {
            return content;
        }

        public void setContent(JsonNode content) {
            this.content = content;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }

    public static class DataProductRes {
        private String uuid;
        private String fqn;
        private String domain;
        private String name;
        private String displayName;
        private String description;
        private String validationState;

        public DataProductRes() {
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getFqn() {
            return fqn;
        }

        public void setFqn(String fqn) {
            this.fqn = fqn;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getValidationState() {
            return validationState;
        }

        public void setValidationState(String validationState) {
            this.validationState = validationState;
        }
    }

}
