package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ApiStandardDefinition;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.SpecificationExtensionPoint;

import javax.persistence.*;

@Data
@Entity(name = "Promises")
@Table(name = "DPV_PORT_PROMISES", schema="ODMREGISTRY")
public class Promises {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "PLATFORM")
    protected String platform;

    @Column(name = "SERVICE_TYPE")
    protected String servicesType;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "API_ID", referencedColumnName = "INSTANCE_ID")
    protected ApiStandardDefinition api;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DEPRECTAION_POLICY_ID", referencedColumnName = "ID")
    protected SpecificationExtensionPoint deprecationPolicy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SLO_ID", referencedColumnName = "ID")
    protected SpecificationExtensionPoint slo;
}
