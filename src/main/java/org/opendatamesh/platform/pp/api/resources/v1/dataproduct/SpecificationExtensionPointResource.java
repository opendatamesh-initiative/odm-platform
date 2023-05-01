package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecificationExtensionPointResource {
    @JsonProperty("description")
    private String description;

    @JsonProperty("externalDocs")
    private ExternalResourceResource externalDocs;
}
