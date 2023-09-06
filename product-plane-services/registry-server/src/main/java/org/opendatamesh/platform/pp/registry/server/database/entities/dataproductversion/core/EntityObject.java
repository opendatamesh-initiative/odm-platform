package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class EntityObject {

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

    public abstract String getId();
}
