package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;


import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "InfrastructuralComponent")
@Table(name = "DPV_INFRA_COMPONENTS", schema="PUBLIC")
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
    @CollectionTable(name = "DPV_INFRA_COMPONENT_DEPENDENCIES", schema="PUBLIC", joinColumns = @JoinColumn(name = "COMPONENT_ID"))
    @Column(name = "DEPENDS_ON_COMPONENT_ID") 
    private List<String> dependsOn = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_INFRA_COMPONENT_TAGS", schema="PUBLIC", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;

    public boolean hasProvisionInfo() {
        return provisionInfo != null;
    }

    public boolean hasProvisionInfoTemplateDefinition() {
        return hasProvisionInfo() && provisionInfo.hasTemplateDefinition();
    }
}
