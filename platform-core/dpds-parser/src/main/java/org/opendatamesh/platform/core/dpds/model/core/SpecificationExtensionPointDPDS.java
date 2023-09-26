package org.opendatamesh.platform.core.dpds.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecificationExtensionPointDPDS {
    @JsonProperty("description")
    @Schema(description = "Specification Extension Point description", required = true)
    private String description;

    @JsonProperty("externalDocs")
    @Schema(description = "Document of the External Resource of the Specification Extension Point", required = true)
    private ExternalResourceDPDS externalDocs;
}
