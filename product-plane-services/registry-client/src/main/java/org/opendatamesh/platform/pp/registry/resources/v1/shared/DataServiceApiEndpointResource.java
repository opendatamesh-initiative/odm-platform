package org.opendatamesh.platform.pp.registry.resources.v1.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataServiceApiEndpointResource {

    @JsonProperty("name")
    private String name; 
    
    @JsonProperty("mediaType")
    private String mediaType;    

    @JsonProperty("schema")
    private String schema;    
    
}
