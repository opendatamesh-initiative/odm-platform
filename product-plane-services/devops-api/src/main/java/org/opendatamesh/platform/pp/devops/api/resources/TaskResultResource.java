package org.opendatamesh.platform.pp.devops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResultResource {

    @JsonProperty("status")
    @Schema(description = "Status of the Task")
    TaskResultStatus status;

    @JsonProperty("results")
    @Schema(description = "Optional JSON results of the Task execution returned from the executor")
    String results;

    @JsonProperty("errors")
    @Schema(description = "Optional textual set of errors of the Task execution returned from the executor")
    String errors;

}
