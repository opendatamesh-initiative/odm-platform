package org.opendatamesh.platform.pp.policy.server.adapter.dataproductinit.events.emitted;

import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;

public class DataProductInitializationRejectedEventRes {
    private final String resourceType = "DATA_PRODUCT";
    private String resourceIdentifier;
    private final String type = NotificationV2EventType.DATA_PRODUCT_INITIALIZATION_REJECTED.getValue();
    private final String eventTypeVersion = "v2.0.0";
    private EventContent eventContent;

    public DataProductInitializationRejectedEventRes() {
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
    }
}
