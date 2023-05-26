package org.opendatamesh.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResource {

    @JsonProperty("id")
    Long id;

    @JsonProperty("event")
    EventResource event;

    @JsonProperty("status")
    private NotificationStatus status;

    @JsonProperty("processingOutput")
    private String processingOutput;

    @JsonProperty("receivedAt")
    private Date receivedAt;

    @JsonProperty("processedAt")
    private Date processedAt;

}
