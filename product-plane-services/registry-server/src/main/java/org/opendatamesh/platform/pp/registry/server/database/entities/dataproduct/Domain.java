package org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct;

import lombok.Data;

import javax.persistence.*;

import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;

import java.io.Serializable;
import java.util.Date;


@Data
@Entity(name = "Domain")
@Table(name = "DOMAINS", schema="ODMREGISTRY")
public class Domain implements Cloneable, Serializable {

    @Id
    @Column(name = "DOMAIN_ID")
    private String id;

    @Column(name="FQN")
    protected String fullyQualifiedName;

    @Enumerated(EnumType.STRING)
    @Column(name="ENTITY_TYPE")
    protected EntityTypeDPDS entityType;

    @Column(name="NAME")
    protected String name;

    @Column(name="DISPLAY_NAME")
    protected String displayName;

    @Column(name="SUMMARY")
    protected String summary;

    @Column(name="DESCRIPTION")
    protected String description;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        entityType = EntityTypeDPDS.DOMAIN;
    }

    @PostLoad
    protected void onRead() {
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}