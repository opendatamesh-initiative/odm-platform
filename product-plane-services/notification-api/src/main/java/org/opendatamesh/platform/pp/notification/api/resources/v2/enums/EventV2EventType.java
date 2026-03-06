package org.opendatamesh.platform.pp.notification.api.resources.v2.enums;

import java.util.Locale;

public enum EventV2EventType {
    // Data Product Events
    DATA_PRODUCT_INITIALIZATION_REQUESTED,
    DATA_PRODUCT_INITIALIZATION_APPROVED,
    DATA_PRODUCT_INITIALIZATION_REJECTED,
    DATA_PRODUCT_INITIALIZED,
    DATA_PRODUCT_DELETED,
    // Data Product Version Events
    DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED,
    DATA_PRODUCT_VERSION_PUBLICATION_APPROVED,
    DATA_PRODUCT_VERSION_PUBLICATION_REJECTED,
    DATA_PRODUCT_VERSION_PUBLISHED,
    DATA_PRODUCT_VERSION_DELETED;

    public static EventV2EventType fromString(String value) {
        return EventV2EventType.valueOf(value.toUpperCase(Locale.ROOT));
    }
}