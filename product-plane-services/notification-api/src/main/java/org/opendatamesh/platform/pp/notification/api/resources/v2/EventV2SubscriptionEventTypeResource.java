package org.opendatamesh.platform.pp.notification.api.resources.v2;

import io.swagger.v3.oas.annotations.media.Schema;

public class EventV2SubscriptionEventTypeResource {
    @Schema(description = "Type of the event type subscribed to", example = "DATA_PRODUCT_CREATED")
    private String eventType;

    public EventV2SubscriptionEventTypeResource() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
