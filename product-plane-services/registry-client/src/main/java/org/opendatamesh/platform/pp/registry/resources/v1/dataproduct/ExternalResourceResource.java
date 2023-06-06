package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalResourceResource {

    @JsonProperty("description")
    private String description;

    @JsonProperty("mediaType")
    private String mediaType;

    @JsonProperty("$href")
    private String href;

}