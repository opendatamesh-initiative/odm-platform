package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity(name = "DeployInfo")
@Table(name = "DPV_APP_COMPONENT_DEPLOY_INFOS", schema="PUBLIC")
public class DeployInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne(mappedBy = "deployInfo")
    ApplicationComponent appComponent;
   
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
    private ExternalResource service;

     @Column(name = "TEMPLATE_ID")
    protected Long templateId;

    @Transient
    protected StandardDefinition template;

    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;

    public boolean hasTemplate() {
        return template != null;
    }

    public boolean hasTemplateDefinition() {
        return hasTemplate() && template.hasDefinition();
    }
}
