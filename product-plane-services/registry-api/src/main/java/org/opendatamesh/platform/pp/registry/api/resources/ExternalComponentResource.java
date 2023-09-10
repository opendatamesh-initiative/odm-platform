package org.opendatamesh.platform.pp.registry.api.resources;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

// API & TEMPLATES:    todo make it abstract
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalComponentResource {

    @JsonProperty("id")
    String id;

    @JsonProperty("fullyQualifiedName")
    protected String fullyQualifiedName;

    @JsonProperty("entityType")
    protected String entityType;
    
    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("specification")
    private String specification;

    @JsonProperty("specificationVersion")
    private String specificationVersion;

    @JsonProperty("definitionMediaType")
    private String definitionMediaType;

    @JsonProperty("definition")
    private String definition;

    @EqualsAndHashCode.Exclude
    @JsonProperty("createdAt")
    protected Date createdAt;

    @EqualsAndHashCode.Exclude
    @JsonProperty("updatedAt")
    protected Date updatedAt;
}
