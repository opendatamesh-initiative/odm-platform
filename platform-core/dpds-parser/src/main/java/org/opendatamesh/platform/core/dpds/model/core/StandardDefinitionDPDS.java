package org.opendatamesh.platform.core.dpds.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;


@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardDefinitionDPDS extends ComponentDPDS {

    @JsonProperty("specification")
    @Schema(description = "Standard Definition specification", required = true)
    private String specification;

    @JsonProperty("specificationVersion")
    @Schema(description = "Standard Definition specification version", required = true)
    private String specificationVersion;

    @JsonProperty("definition")
    @Schema(description = "Definition Reference object of the Standard Definition", required = true)
    private DefinitionReferenceDPDS definition;
}
