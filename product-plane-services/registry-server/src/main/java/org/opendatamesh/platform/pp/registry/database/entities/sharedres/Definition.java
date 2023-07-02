package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;

import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.StandardDefinition;

import lombok.Data;

@Data
@Entity(name = "Definition")
@Table(name = "DEFINITIONS", schema="PUBLIC")
public class Definition {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SPECIFICATION")
    private String specification;

    @Column(name = "SPECIFICATION_VERSION")
    private String specificationVersion;

    @Column(name = "CONTENT_MEDIA_TYPE")
    private String contentMediaType;

    @Column(name = "CONTENT")
    private String content;

    public Definition() {}

    public Definition(String type, StandardDefinition standardDefinition) {
       if(standardDefinition != null) {
            this.name = standardDefinition.getName();
            this.version = standardDefinition.getVersion();
            this.description = standardDefinition.getDescription();
            this.specification = standardDefinition.getSpecification();
            this.specificationVersion = standardDefinition.getSpecificationVersion();
            this.content = standardDefinition.getDefinition().getRawContent();
        }
        
    }
    
   
}