package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.SpecificationExtensionPoint;

import javax.persistence.*;

@Data
@Entity(name = "Expectations")
@Table(name = "DPV_PORT_EXPECTATIONS", schema="ODMREGISTRY")
public class Expectations {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIENCE_ID", referencedColumnName = "ID")
    protected SpecificationExtensionPoint audience;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USAGE_ID", referencedColumnName = "ID")
    protected SpecificationExtensionPoint usage;
}
