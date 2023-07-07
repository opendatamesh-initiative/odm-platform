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

    @JsonProperty("schema")
    private Schema schema;    
    
    @Data
    public static class Schema {

        // Name and version are not part of the schema so are always null at the moment
        // In the future whenever possible extract name and version from meta-schema (see schemata)
        @JsonProperty("name")
        private String name; 
        @JsonProperty("version")
        private String version; 

        @JsonProperty("mediaType")
        private String mediaType; 

        @JsonProperty("content")
        private String content;    
    }
}
