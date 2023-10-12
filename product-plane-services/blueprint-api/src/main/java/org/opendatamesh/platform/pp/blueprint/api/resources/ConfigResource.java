package org.opendatamesh.platform.pp.blueprint.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigResource {

    @JsonProperty("config")
    @Schema(description = "List of key-value pairs representing the parameters needed for templating a blueprint")
    Map<String, String> config;

}
