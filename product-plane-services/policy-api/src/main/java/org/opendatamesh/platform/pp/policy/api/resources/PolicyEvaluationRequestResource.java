package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEvaluationRequestResource {
    @JsonProperty("type")
    @Schema(description = "Resource type")
    private ResourceType resourceType;

    @JsonProperty("event")
    @Schema(description = "The event that triggered the evaluation")
    private EventType event;

    @JsonProperty("currentState")
    @Schema(description = "The current state")
    private String currentState;

    @JsonProperty("afterState")
    @Schema(description = "The next state")
    private String afterState;

    public enum EventType {
        DATA_PRODUCT_CREATION,
        DATA_PRODUCT_UPDATE,
        ACTIVITY_STAGE_TRANSITION,
        TASK_EXECUTOR_INITIAL_CALL,
        TASK_EXECUTOR_FINAL_CALL
    }

    public enum ResourceType {
        DATA_PRODUCT,
        ACTIVITY,
        TASK_RESULT
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getAfterState() {
        return afterState;
    }

    public void setAfterState(String afterState) {
        this.afterState = afterState;
    }
}
