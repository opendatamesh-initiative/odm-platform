package org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.received;

import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;

import java.util.Date;

public class DataProductInitializationRequestedEventRes {
    private final String resourceType = "DATA_PRODUCT";
    private String resourceIdentifier;
    private final String type = NotificationV2EventType.DATA_PRODUCT_INITIALIZATION_REQUESTED.getValue();
    private final String eventTypeVersion = "v2.0.0";
    private EventContent eventContent;

    public DataProductInitializationRequestedEventRes() {
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
        private DataProductRes dataProduct;

        public EventContent() {
        }

        public DataProductRes getDataProduct() {
            return dataProduct;
        }

        public void setDataProduct(DataProductRes dataProduct) {
            this.dataProduct = dataProduct;
        }
    }

    public static class DataProductRes {
        private String uuid;
        private String fqn;
        private String domain;
        private String name;
        private String displayName;
        private String description;
        private Date createdAt;
        private Date updatedAt;

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

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
