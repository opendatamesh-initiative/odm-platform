package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardDefinitionResource {

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("description")
    private String description;

    @JsonProperty("specification")
    private String specification;

    @JsonProperty("specificationVersion")
    private String specificationVersion;

    /*
    @JsonProperty("definitionId")
    private Long definitionId;
    */
    private ReferenceObjectResource definition;

    @JsonProperty("externalDocs")
    private ExternalResourceResource externalDocs;
}
