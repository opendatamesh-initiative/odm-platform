package org.opendatamesh.platform.pp.policy.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;

@Data
public class TaskResultEvent {

    @JsonProperty("afterState")
    TaskResultEventTypeResource afterState;

    @JsonProperty("currentState")
    TaskResultEventTypeResource currentState;

}
