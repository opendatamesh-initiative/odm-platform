package org.opendatamesh.platform.pp.notification.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class NotificationSearchOptions {

    @JsonProperty("eventType")
    @Schema(description = "The type of the event encapsulated in the notification")
    private String eventType;

    @JsonProperty("notificationStatus")
    @Schema(description = "The status of the notification")
    private String notificationStatus;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

}
