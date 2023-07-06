package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

import javax.persistence.Transient;

@Data
public class ApiDefinitionEndpoint {
    
    @Transient
    private String name; 

    @Transient
    private String outputMediaType;    
    
    @Transient
    private Schema schema;    

    @Data
    public static class Schema {
         @Transient
        private String name; 

         @Transient
        private String version; 

         @Transient
        private String mediaType; 

         @Transient
        private String content;    
    }
}
