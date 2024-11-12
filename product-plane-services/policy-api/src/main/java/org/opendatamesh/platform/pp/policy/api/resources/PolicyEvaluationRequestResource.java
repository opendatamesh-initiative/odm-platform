package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;


public class PolicyEvaluationRequestResource {
    @JsonProperty("resourceType")
    @Schema(description = "Resource type")
    private ResourceType resourceType;

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
    private JsonNode currentState;

    @JsonProperty("afterState")
    @Schema(description = "The next state")
    private JsonNode afterState;

    public enum EventType {
        DATA_PRODUCT_CREATION,
        DATA_PRODUCT_UPDATE,
        DATA_PRODUCT_VERSION_CREATION,
        ACTIVITY_STAGE_TRANSITION,
        TASK_EXECUTION_RESULT,
        ACTIVITY_EXECUTION_RESULT,
    }

    public enum ResourceType {
        DATA_PRODUCT_DESCRIPTOR,
        ACTIVITY_STAGE_TRANSITION,
        ACTIVITY_EXECUTION_RESULT,
        TASK_EXECUTION_RESULT
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

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

    public JsonNode getCurrentState() {
        return currentState;
    }

    public void setCurrentState(JsonNode currentState) {
        this.currentState = currentState;
    }

    public JsonNode getAfterState() {
        return afterState;
    }

    public void setAfterState(JsonNode afterState) {
        this.afterState = afterState;
    }
}
