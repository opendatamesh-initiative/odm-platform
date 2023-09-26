package org.opendatamesh.platform.pp.devops.api.resources;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityStatusResource {
    @JsonProperty("status")
    @Schema(description = "Status of the Activity")
    ActivityStatus status;
}