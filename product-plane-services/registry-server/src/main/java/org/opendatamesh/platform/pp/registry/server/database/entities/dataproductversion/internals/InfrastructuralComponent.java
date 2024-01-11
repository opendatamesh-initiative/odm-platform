package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.InternalComponent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@Entity(name = "InfrastructuralComponent")
@Table(name = "DPV_INFRA_COMPONENTS", schema="ODMREGISTRY")
public class InfrastructuralComponent extends InternalComponent implements Cloneable {
    
    @Column(name = "PLATFORM")
    private String platform;

    @Column(name = "INFRASTRUCTURE_TYPE")    
    private String infrastructureType;

    
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "DPV_INFRA_COMPONENT_DEPENDENCIES", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "COMPONENT_ID"))
    @Column(name = "DEPENDS_ON_COMPONENT_ID") 
    private List<String> dependsOn = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_INFRA_COMPONENT_TAGS", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;

}
