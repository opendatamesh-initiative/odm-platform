package org.opendatamesh.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResource {
    @JsonProperty("id")
    Long id;
    @JsonProperty("type")
    private EventType type;
    @JsonProperty("entityId")
    private String entityId;
    @JsonProperty("beforeState")
    private String beforeState;
    @JsonProperty("afterState")
    private String afterState;
    @JsonProperty("time")
    private Date time;

    public EventResource (EventType type, String entityId, String beforeState, String afterState) {
        this.type = type;
        this.entityId = entityId;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.time = new Date();
    }

}
