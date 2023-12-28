package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VariableResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Variable ID")
    private Long id;

    @JsonProperty("name")
    @Schema(description = "Variable name", required = true)
    private String variableName;

    @JsonProperty("value")
    @Schema(description = "Variable value", required = true)
    private String variableValue;

}