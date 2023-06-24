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
    private String schemaMediaType;    

     @Transient
    private String schema;       
}
