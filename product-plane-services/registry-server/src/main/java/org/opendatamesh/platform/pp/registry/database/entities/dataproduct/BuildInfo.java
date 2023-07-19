package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity( name = "BuildInfo" )
@Table( name = "DPV_APP_COMPONENT_BUILD_INFOS", schema="ODMREGISTRY")
public class BuildInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne(mappedBy = "buildInfo")
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
