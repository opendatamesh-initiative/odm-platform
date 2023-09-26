package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Schema ID")
    Long id;

    @JsonProperty("name")
    @Schema(description = "Schema name", required = true)
    private String name;

    @JsonProperty("version")
    @Schema(description = "Schema version", required = true)
    private String version;

    @JsonProperty("mediaType")
    @Schema(description = "Media Type of the Schema", required = true)
    private String mediaType; 

    @JsonProperty("content")
    @Schema(description = "Content of the Schema", required = true)
    private String content;  
}
