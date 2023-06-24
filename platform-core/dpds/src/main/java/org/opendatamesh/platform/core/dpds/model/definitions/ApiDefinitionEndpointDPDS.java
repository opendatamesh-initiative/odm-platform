package org.opendatamesh.platform.core.dpds.model.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinitionEndpointDPDS {

    @JsonProperty("name")
    private String name; 

    @JsonProperty("outputMediaType")
    private String outputMediaType;    
    
    @JsonProperty("schemaMediaType")
    private String schemaMediaType;    

    @JsonProperty("schema")
    private String schema;    
    
}
