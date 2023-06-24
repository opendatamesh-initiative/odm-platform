package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import javax.persistence.Transient;

public class Schema {
    @Transient
    private String name; 

    @Transient
    private String outputMediaType;    
    
    @Transient
    private String schemaMediaType;    

    @Transient
    private String schema;     
}
