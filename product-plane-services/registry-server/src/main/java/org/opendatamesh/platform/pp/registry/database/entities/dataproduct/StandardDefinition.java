package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "StandardDefinition")
@Table(name = "DPDS_STANDARD_DEFINITIONS", schema="PUBLIC")
public class StandardDefinition {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SPECIFICATION")
    private String specification;

    @Column(name = "SPECIFICATION_VERSION")
    private String specificationVersion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DEFINITION_ID", referencedColumnName = "ID")
    private ReferenceObject definition;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;
}