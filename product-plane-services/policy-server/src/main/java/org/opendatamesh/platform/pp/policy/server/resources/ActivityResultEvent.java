package org.opendatamesh.platform.pp.policy.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.opendatamesh.platform.pp.policy.api.resources.events.ActivityResultEventTypeResource;

@Data
public class ActivityResultEvent {

    @JsonProperty("afterState")
    ActivityResultEventTypeResource afterState;

    @JsonProperty("currentState")
    ActivityResultEventTypeResource currentState;

}
