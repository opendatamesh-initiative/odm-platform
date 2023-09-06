package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@MappedSuperclass
public class StandardDefinition extends ExternalComponent {
      
    @Column(name="SPECIFICATION")
    private String specification;
    
    @Column(name="SPECIFICATION_VERSION")
    private String specificationVersion;
    
    @Transient
    private DefinitionReference definition;

    @Transient
    private ExternalResource externalDocs;

    public boolean hasDefinition() {
       return definition != null; 
    }
}