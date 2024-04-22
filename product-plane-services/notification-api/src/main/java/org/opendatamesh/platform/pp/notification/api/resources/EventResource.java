package org.opendatamesh.platform.pp.notification.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Event ID")
    Long id;

    @JsonProperty("type")
    @Schema(description = "Event type", required = true)
    private String type;

    @JsonProperty("entityId")
    @Schema(description = "ID of the Entity subject of the Event", required = true)
    private String entityId;

    @JsonProperty("beforeState")
    @Schema(description = "Entity state before the Event")
    private String beforeState;

    @JsonProperty("afterState")
    @Schema(description = "Entity state after the Event")
    private String afterState;

    @JsonProperty("time")
    @Schema(description = "Event timestamp", required = true)
    private Date time;

    public EventResource () {}

    public EventResource (EventType type, String entityId, String beforeState, String afterState) {
        this.type = type.toString();
        this.entityId = entityId;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.time = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(String beforeState) {
        this.beforeState = beforeState;
    }

    public String getAfterState() {
        return afterState;
    }

    public void setAfterState(String afterState) {
        this.afterState = afterState;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
