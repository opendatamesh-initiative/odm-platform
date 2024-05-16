package org.opendatamesh.platform.pp.policy.server.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.opendatamesh.platform.pp.policy.api.resources.events.DataProductEventTypeResource;

@Data
public class DataProductEvent {

    @JsonProperty("afterState")
    DataProductEventTypeResource afterState;

    @JsonProperty("currentState")
    DataProductEventTypeResource currentState;

}
