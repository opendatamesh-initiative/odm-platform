package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity(name = "Expectations")
@Table(name = "DPDS_PORT_EXPECTATIONS", schema="PUBLIC")
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
