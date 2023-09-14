package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.TemplateStandardDefinition;
import org.opendatamesh.platform.pp.registry.server.utils.HashMapConverter;

import lombok.Data;

@Data
@Entity( name = "LifecycleTaskInfo" )
@Table( name = "DPV_TASK_INFOS", schema="ODMREGISTRY")
public class LifecycleTaskInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STAGE")
    private String stageName;

    @Column(name = "EXEC_ORDER")
    private Integer order;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
    private ExternalResource service;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "INSTANCE_ID")
    protected TemplateStandardDefinition template;

    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;    
    
    public boolean hasTemplate() {
        return template != null;
    }

    public boolean hasTemplateDefinition() {
        return hasTemplate() && template.hasDefinition();
    }
}
