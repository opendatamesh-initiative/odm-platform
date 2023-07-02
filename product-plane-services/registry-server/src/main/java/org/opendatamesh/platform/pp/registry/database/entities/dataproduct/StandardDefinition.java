package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

@Data
public class StandardDefinition {
      
    private String name;

    private String version;

    private String description;

    private String specification;
    
    private String specificationVersion;
    
    private DefinitionReference definition;
    
    private ExternalResource externalDocs;
}