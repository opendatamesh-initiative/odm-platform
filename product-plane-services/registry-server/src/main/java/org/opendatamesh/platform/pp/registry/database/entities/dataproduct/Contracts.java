package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity(name = "Contracts")
@Table(name = "DPV_PORT_CONTRACTS", schema="PUBLIC")
public class Contracts {

        @Id
        @Column(name = "ID")
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        Long id;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "TERMS_AND_CONDITIONS_ID", referencedColumnName = "ID")
        protected SpecificationExtensionPoint termsAndConditions;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "BILLING_POLICY_ID", referencedColumnName = "ID")
        protected SpecificationExtensionPoint billingPolicy;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "SLA_ID", referencedColumnName = "ID")
        protected SpecificationExtensionPoint sla;
}
