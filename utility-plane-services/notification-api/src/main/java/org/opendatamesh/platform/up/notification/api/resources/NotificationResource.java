package org.opendatamesh.platform.up.notification.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResource implements Cloneable{
    
    @JsonProperty("id")
    @Schema(description = "Auto generated ID of the Notification")
    Long id;

    @JsonProperty("event")
    @Schema(description = "Event object of the Notification", required = true)
    EventResource event;

    @JsonProperty("status")
    @Schema(description = "Status of the Notification")
    private NotificationStatus status;

    @JsonProperty("processingOutput")
    @Schema(description = "Output of the Notification processing phase")
    private String processingOutput;

    @JsonProperty("receivedAt")
    @Schema(description = "Timpestamp of the Notification reception")
    private Date receivedAt;

    @JsonProperty("processedAt")
    @Schema(description = "Timpestamp of the Notification processing phase")
    private Date processedAt;
}
