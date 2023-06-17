package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.*;

import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;

import lombok.Data;

import java.util.*;

@Data
@MappedSuperclass
public class Component {

    @Id
    @Column(name="ID")
    String id;

    @Column(name="FQN")
    protected String fullyQualifiedName;

    @Enumerated(EnumType.STRING)
    @Column(name="ENTITY_TYPE")
    protected EntityTypeDPDS entityType;

    @Column(name="NAME")
    protected String name;

    @Column(name="VERSION")
    protected String version;

    @Column(name="DISPLAY_NAME")
    protected String displayName;

    @Column(name="DESCRIPTION")
    protected String description;

    @Column(name="COMPONENT_GROUP")
    protected String componentGroup;

    @Column(name="CREATED_AT")
    protected Date createdAt;
   
    @Column(name="UPDATED_AT")
    protected Date updatedAt;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;

    public void setFullyQualifiedName(String fqn) {
        fullyQualifiedName = fqn;
        setId(UUID.nameUUIDFromBytes(fqn.getBytes()).toString());
    }

    @PrePersist
    protected void onCreate() {
        setId(UUID.nameUUIDFromBytes(this.getFullyQualifiedName().getBytes()).toString());
        createdAt = new Date();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}