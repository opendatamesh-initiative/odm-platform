package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.*;

import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity(name = "DeployInfo")
@Table(name = "DPDS_APP_COMPONENT_DEPLOY_INFOS", schema="PUBLIC")
public class DeployInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne(mappedBy = "deployInfo")
    ApplicationComponent appComponent;
    /* 
    @Id
    @Column(name = "APP_COMPONENT_ID")
    private String id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "APP_COMPONENT_ID")
    private ApplicationComponent appComponent;
    */
   
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
    private ExternalResource service;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "ID")
    private ExternalResource template;


    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;

    private static final Logger logger = LoggerFactory.getLogger(DeployInfo.class);


    public DeployInfo() {
        service= new ExternalResource();
        template = new ExternalResource();
        configurations = new HashMap<>();
    }

    public DeployInfo(ExternalResource service, ExternalResource template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }

    @Override
    public String toString() {
        return "DeployInfo{" +
                "id=" + id +
                ", service=" + service +
                ", template=" + template +
                ", configurations=" + configurations +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating appComponent [" + getId() + "]");
    }
}
