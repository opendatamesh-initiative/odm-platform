package org.opendatamesh.platform.pp.notification.api.resources.v2.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventV2EventTypeVersion {
    V1_0_0("V1.0.0"),
    V2_0_0("V2.0.0");

    private final String label;

    EventV2EventTypeVersion(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static EventV2EventTypeVersion fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("EventTypeVersion value cannot be null or empty");
        }

        for (EventV2EventTypeVersion version : EventV2EventTypeVersion.values()) {
            if (version.label.equalsIgnoreCase(value)) {
                return version;
            }
        }

        throw new IllegalArgumentException("Unknown EventTypeVersion: " + value);
    }
}