package org.opendatamesh.platform.pp.notification.api.resources.v2.enums;

import java.util.Locale;

public enum EventV2ResourceType {
    DATA_PRODUCT,
    DATA_PRODUCT_VERSION;

    public static EventV2ResourceType fromString(String value) {
        return EventV2ResourceType.valueOf(value.toUpperCase(Locale.ROOT));
    }
}
