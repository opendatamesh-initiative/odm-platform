package org.opendatamesh.platform.pp.policy.server.adapter.dataproductversionpublish.events.emitted;

import org.opendatamesh.platform.pp.policy.server.adapter.NotificationV2EventType;

public class DataProductVersionPublicationApprovedEventRes {
    private Long sequenceId;
    private final String resourceType = "DATA_PRODUCT_VERSION";
    private String resourceIdentifier;
    private final String type = NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_APPROVED.getValue();
    private final String eventTypeVersion = "v2.0.0";
    private EventContent eventContent;

    public DataProductVersionPublicationApprovedEventRes() {
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
        private DataProductVersionRes dataProductVersion;

        public EventContent() {
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
        private String tag;
        private DataProductRes dataProduct;
      

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

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
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
