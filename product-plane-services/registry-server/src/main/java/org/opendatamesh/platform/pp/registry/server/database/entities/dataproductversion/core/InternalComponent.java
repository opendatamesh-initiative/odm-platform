package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@Data
@EqualsAndHashCode(callSuper=true)
@MappedSuperclass
public abstract class InternalComponent extends Component {
    @Id
    @Column(name="ID")
    String id;
}