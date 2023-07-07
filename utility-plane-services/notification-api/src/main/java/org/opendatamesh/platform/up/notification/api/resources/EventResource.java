package org.opendatamesh.platform.up.notification.api.resources;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResource {
    @JsonProperty("id")
    Long id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("entityId")
    private String entityId;

    @JsonProperty("beforeState")
    private String beforeState;

    @JsonProperty("afterState")
    private String afterState;

    @JsonProperty("time")
    private Date time;

    public EventResource () {}

    public EventResource (EventType type, String entityId, String beforeState, String afterState) {
        this.type = type.toString();
        this.entityId = entityId;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.time = new Date();
    }
}