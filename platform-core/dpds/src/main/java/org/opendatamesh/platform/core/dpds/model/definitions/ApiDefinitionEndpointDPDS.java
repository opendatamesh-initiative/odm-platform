package org.opendatamesh.platform.core.dpds.model.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinitionEndpointDPDS {

    @JsonProperty("name")
    private String name; 
    
    @JsonProperty("mediaType")
    private String mediaType;    

    @JsonProperty("schema")
    private String schema;    
    
}
