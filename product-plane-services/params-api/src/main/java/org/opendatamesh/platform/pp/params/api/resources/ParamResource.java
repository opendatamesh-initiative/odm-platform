package org.opendatamesh.platform.pp.params.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParamResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Param ID")
    Long id;

    @JsonProperty("paramName")
    @Schema(description = "Name of the parameter (e.g., 'server.port')", required = true)
    private String paramName;

    @JsonProperty("paramValue")
    @Schema(description = "Value for the parameter", required = true)
    private String paramValue;

    @JsonProperty("displayName")
    @Schema(description = "Human-readable name of the parameter")
    private String displayName;

    @JsonProperty("description")
    @Schema(description = "Description of the parameter")
    private String description;

    @JsonProperty("secret")
    @Schema(description = "Whether the value of the parameter is a secret or not", defaultValue = "false")
    private Boolean secret;

    @JsonProperty("createdAt")
    @Schema(description = "Timestamp of the Param creation")
    private Date createdAt;

    @JsonProperty("startedAt")
    @Schema(description = "Timestamp of the last Param update")
    private Date updatedAt;

}