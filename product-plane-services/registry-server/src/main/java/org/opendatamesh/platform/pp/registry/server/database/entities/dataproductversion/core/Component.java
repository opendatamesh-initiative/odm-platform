package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper=true)
@MappedSuperclass
public abstract class Component extends EntityObject {

    @Column(name="COMPONENT_GROUP")
    protected String componentGroup;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;

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