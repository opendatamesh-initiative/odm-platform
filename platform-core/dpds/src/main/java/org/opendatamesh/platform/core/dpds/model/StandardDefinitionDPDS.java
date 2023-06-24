package org.opendatamesh.platform.core.dpds.model;

import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardDefinitionDPDS {

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

    @JsonProperty("definition")
    private DefinitionReferenceDPDS definition;

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;
}
