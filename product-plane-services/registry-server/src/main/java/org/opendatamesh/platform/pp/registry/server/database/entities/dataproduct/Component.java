package org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct;

import lombok.Data;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@MappedSuperclass
public class Component {

    @Id
    @Column(name="ID")
    String id;

    @Column(name="FQN")
    protected String fullyQualifiedName;

    //@Enumerated(EnumType.STRING)
    @Column(name="ENTITY_TYPE")
    protected String entityType;

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

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;

    @Column(name="CREATED_AT")
    protected Date createdAt;
   
    @Column(name="UPDATED_AT")
    protected Date updatedAt;

    
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