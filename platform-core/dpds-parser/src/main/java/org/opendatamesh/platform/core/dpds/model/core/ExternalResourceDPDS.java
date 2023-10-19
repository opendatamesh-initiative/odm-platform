package org.opendatamesh.platform.core.dpds.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalResourceDPDS {

    @JsonProperty("description")
    @Schema(description = "External Resource description", required = true)
    private String description;

    @JsonProperty("mediaType")
    @Schema(description = "Media Type of the External Resource", required = true)
    private String mediaType;

    @JsonProperty("$href")
    @Schema(description = "URL of the External Resource", required = true)
    private String href;

}
