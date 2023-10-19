package org.opendatamesh.platform.pp.registry.api.resources;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

// API & TEMPLATES:    todo make it abstract
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalComponentResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated External Component ID")
    String id;

    @JsonProperty("fullyQualifiedName")
    @Schema(description = "Domain fully qualified name", required = true)
    protected String fullyQualifiedName;

    @JsonProperty("entityType")
    @Schema(description = "Entity type", required = true)
    protected String entityType;
    
    @JsonProperty("name")
    @Schema(description = "External Component name", required = true)
    private String name;

    @JsonProperty("version")
    @Schema(description = "External Component version", required = true)
    private String version;

    @JsonProperty("displayName")
    @Schema(description = "External Component name to display", required = true)
    private String displayName;

    @JsonProperty("description")
    @Schema(description = "External Component description", required = true)
    private String description;

    @JsonProperty("specification")
    @Schema(description = "External Component specification", required = true)
    private String specification;

    @JsonProperty("specificationVersion")
    @Schema(description = "External Component specification version", required = true)
    private String specificationVersion;

    @JsonProperty("definitionMediaType")
    @Schema(description = "Media Type of the definition of the External Component", required = true)
    private String definitionMediaType;

    @JsonProperty("definition")
    @Schema(description = "Definition of the External Component", required = true)
    private String definition;

    @EqualsAndHashCode.Exclude
    @JsonProperty("createdAt")
    @Schema(description = "Creation timestamp of the External Component", required = true)
    protected Date createdAt;

    @EqualsAndHashCode.Exclude
    @JsonProperty("updatedAt")
    @Schema(description = "Update timestamp of the External Component", required = true)
    protected Date updatedAt;
}
