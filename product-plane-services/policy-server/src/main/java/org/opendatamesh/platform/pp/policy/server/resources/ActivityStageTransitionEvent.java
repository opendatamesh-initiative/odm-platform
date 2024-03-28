package org.opendatamesh.platform.pp.policy.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.opendatamesh.platform.pp.policy.api.resources.events.ActivityStageTransitionEventTypeResource;

@Data
public class ActivityStageTransitionEvent {

    @JsonProperty("afterState")
    ActivityStageTransitionEventTypeResource afterState;

    @JsonProperty("currentState")
    ActivityStageTransitionEventTypeResource currentState;

}
