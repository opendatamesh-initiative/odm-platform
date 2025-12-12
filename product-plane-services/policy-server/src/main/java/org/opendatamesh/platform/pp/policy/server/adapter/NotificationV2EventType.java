package org.opendatamesh.platform.pp.policy.server.adapter;

public enum NotificationV2EventType {
    //Received Events
    DATA_PRODUCT_INITIALIZATION_REQUESTED("DATA_PRODUCT_INITIALIZATION_REQUESTED"),
    DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED("DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED"),
    //Emitted Events
    DATA_PRODUCT_INITIALIZATION_APPROVED("DATA_PRODUCT_INITIALIZATION_APPROVED"),
    DATA_PRODUCT_INITIALIZATION_REJECTED("DATA_PRODUCT_INITIALIZATION_REJECTED"),
    DATA_PRODUCT_VERSION_PUBLICATION_APPROVED("DATA_PRODUCT_VERSION_PUBLICATION_APPROVED"),
    DATA_PRODUCT_VERSION_PUBLICATION_REJECTED("DATA_PRODUCT_VERSION_PUBLICATION_REJECTED");

    private final String value;

    NotificationV2EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
