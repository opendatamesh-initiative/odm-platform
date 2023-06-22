package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecificationExtensionPointDPDS {
    @JsonProperty("description")
    private String description;

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;
}
