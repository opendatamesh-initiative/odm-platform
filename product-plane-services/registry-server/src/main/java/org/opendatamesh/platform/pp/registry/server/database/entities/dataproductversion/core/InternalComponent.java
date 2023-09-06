package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;


@Data
@EqualsAndHashCode(callSuper=true)
@MappedSuperclass
public abstract class InternalComponent extends Component {
    @Id
    @Column(name="ID")
    String id;
}