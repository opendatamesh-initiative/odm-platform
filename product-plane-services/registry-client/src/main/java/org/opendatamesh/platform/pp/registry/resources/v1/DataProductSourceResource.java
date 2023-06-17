package org.opendatamesh.platform.pp.registry.resources.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductSourceResource {
    @JsonProperty("uri")
    private String uri;    
}
