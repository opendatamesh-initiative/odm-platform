package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

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
import javax.persistence.Transient;

import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;


import lombok.Data;

@Data
@Entity( name = "ActivityInfo" )
@Table( name = "DPV_ACTIVITY_INFOS", schema="ODMREGISTRY")
public class LifecycleActivityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "STAGE")
    private String stageName;
    
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

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;    



    public boolean hasTemplate() {
        return template != null;
    }

    public boolean hasTemplateDefinition() {
        return hasTemplate() && template.hasDefinition();
    }
}
