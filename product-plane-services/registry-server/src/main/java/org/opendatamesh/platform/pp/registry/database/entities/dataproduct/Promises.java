package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity(name = "Promises")
@Table(name = "DPDS_PORT_PROMISES", schema="PUBLIC")
public class Promises {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "PLATFORM")
    protected String platform;

    @Column(name = "SERVICE_TYPE")
    protected String servicesType;

    @Column(name = "API_ID")
    protected Long apiId;

    @Transient
    protected StandardDefinition api;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DEPRECTAION_POLICY_ID", referencedColumnName = "ID")
    protected SpecificationExtensionPoint deprecationPolicy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SLO_ID", referencedColumnName = "ID")
    protected SpecificationExtensionPoint slo;
}
