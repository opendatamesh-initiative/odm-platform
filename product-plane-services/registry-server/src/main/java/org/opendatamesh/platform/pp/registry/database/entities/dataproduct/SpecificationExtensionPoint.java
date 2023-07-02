package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity(name = "SpecificationExtensionPoint")
@Table(name = "DPV_SPEC_EXTENSION_POINTS", schema="PUBLIC")
public class SpecificationExtensionPoint {
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "DESCRIPTION")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;
}
