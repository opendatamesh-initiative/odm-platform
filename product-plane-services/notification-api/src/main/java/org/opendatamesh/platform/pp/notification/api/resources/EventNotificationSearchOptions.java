package org.opendatamesh.platform.pp.notification.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;

public class EventNotificationSearchOptions {

    @JsonProperty("eventType")
    @Schema(description = "The type of the event encapsulated in the notification")
    private EventType eventType;

    @JsonProperty("notificationStatus")
    @Schema(description = "The status of the notification")
    private EventNotificationStatus notificationStatus;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventNotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(EventNotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

}
