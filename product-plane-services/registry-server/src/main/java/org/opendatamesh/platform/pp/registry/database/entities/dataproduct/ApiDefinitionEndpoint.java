package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.Transient;

import lombok.Data;

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
