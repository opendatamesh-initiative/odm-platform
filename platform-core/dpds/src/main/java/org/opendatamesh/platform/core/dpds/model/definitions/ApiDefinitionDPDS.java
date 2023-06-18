package org.opendatamesh.platform.core.dpds.model.definitions;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinitionDPDS {
    @JsonProperty("baseUri")
    URI baseUri;
    
    @JsonProperty("rawContent")
    String rawContent;

    @JsonProperty("endpoints")
    List<ApiDefinitionEndpointDPDS> endpoints;
}
