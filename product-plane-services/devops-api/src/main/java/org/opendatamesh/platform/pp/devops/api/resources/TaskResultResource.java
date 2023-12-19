package org.opendatamesh.platform.pp.devops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResultResource {

    @JsonProperty("status")
    @Schema(description = "Status of the Task")
    TaskResultStatus status;

    @JsonProperty("results")
    @Schema(description = "Optional JSON results of the Task execution returned from the executor")
    Map<String, Object> results;

    @JsonProperty("errors")
    @Schema(description = "Optional textual set of errors of the Task execution returned from the executor")
    String errors;

    public String toJsonString() throws JsonProcessingException {
        return ObjectMapperFactory.JSON_MAPPER.writeValueAsString(this);
    }

}
