package org.opendatamesh.platform.pp.registry.server.database.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "Api")
@Table(name = "APIS", schema="ODMREGISTRY")
public class Api {
    @Id
    @Column(name="ID")
    String id;

    @Column(name="FQN")
    protected String fullyQualifiedName;

    @Column(name="ENTITY_TYPE")
    protected String entityType;
    
    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SPECIFICATION")
    private String specification;

    @Column(name = "SPECIFICATION_VERSION")
    private String specificationVersion;

    @Column(name = "DEFINITION_MEDIA_TYPE")
    private String definitionMediaType;

    @Column(name = "DEFINITION")
    private String definition;

    @Column(name="CREATED_AT")
    protected Date createdAt;

    @Column(name="UPDATED_AT")
    protected Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}