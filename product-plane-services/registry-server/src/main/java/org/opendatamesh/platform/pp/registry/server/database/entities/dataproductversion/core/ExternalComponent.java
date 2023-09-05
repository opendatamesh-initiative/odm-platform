package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@EqualsAndHashCode(callSuper=true)
@MappedSuperclass
public abstract class ExternalComponent  extends Component {
   
    @Id
    @Column(name = "INSTANCE_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long instanceId;

    @Column(name="ID")
    String id;
}