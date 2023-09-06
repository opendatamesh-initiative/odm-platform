package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;


@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardDefinitionDPDS extends ComponentDPDS {

    @JsonProperty("specification")
    private String specification;

    @JsonProperty("specificationVersion")
    private String specificationVersion;

    @JsonProperty("definition")
    private DefinitionReferenceDPDS definition;
}
