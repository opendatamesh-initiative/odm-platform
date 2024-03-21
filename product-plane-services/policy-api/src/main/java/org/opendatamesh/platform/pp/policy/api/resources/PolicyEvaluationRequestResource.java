package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEvaluationRequestResource {
    /*@JsonProperty("resourceType")
    @Schema(description = "Resource type")
    private ResourceType resourceType;*/

    @JsonProperty("dataProductId")
    @Schema(description = "ID of the Data Product evaluated (if the evaluation subject was a Data Product)")
    private String dataProductId;

    @JsonProperty("dataProductVersion")
    @Schema(description = "Version number of the Data Product evaluated (if the evaluation subject was a Data Product)")
    private String dataProductVersion;

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
        TASK_EXECUTION_RESULT,
        ACTIVITY_EXECUTION_RESULT
    }

    /*public enum ResourceType {
        DATA_PRODUCT,
        ACTIVITY,
        TASK_RESULT
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }*/

    public String getDataProductId() {
        return dataProductId;
    }

    public void setDataProductId(String dataProductId) {
        this.dataProductId = dataProductId;
    }

    public String getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(String dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
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
