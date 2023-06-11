package org.opendatamesh.platform.pp.registry.resources.v1.shared;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataServiceApiResource {
    @JsonProperty("baseUri")
    URI baseUri;
    
    @JsonProperty("rawContent")
    String rawContent;

    @JsonProperty("endpoints")
    List<DataServiceApiEndpointResource> endpoints;
}
