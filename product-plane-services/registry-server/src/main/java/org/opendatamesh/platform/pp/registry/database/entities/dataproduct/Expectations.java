package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

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
