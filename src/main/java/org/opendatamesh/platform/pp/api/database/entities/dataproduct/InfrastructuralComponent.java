package org.opendatamesh.platform.pp.api.database.entities.dataproduct;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;

@Data
@Entity(name = "InfrastructuralComponent")
@Table(name = "DPDS_INFRA_COMPONENTS", schema="PUBLIC")
public class InfrastructuralComponent extends Component implements Cloneable {
    
    @Column(name = "PLATFORM")
    private String platform;

    @Column(name = "INFRASTRUCTURE_TYPE")    
    private String infrastructureType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROVISION_INFO_ID", referencedColumnName = "ID")
    private ProvisionInfo provisionInfo;

   
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "DPDS_INFRA_COMPONENT_DEPENDENCIES", schema="PUBLIC", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "DEPENDENCE_ID") 
    private List<String> dependsOn = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPDS_INFRA_COMPONENT_TAGS", schema="PUBLIC", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;
}
