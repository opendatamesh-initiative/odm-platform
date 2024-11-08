package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class PolicyEvaluationEventResource {

    @JsonProperty("event")
    @Schema(description = "A tag, a phase, something that identify in which stages of the lifecycle the Policy must be evaluated")
    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
