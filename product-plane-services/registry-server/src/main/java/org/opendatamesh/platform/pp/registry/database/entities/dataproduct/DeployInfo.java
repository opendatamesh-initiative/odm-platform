package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.HashMap;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "ID")
    private ReferenceObject template;


    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;

    private static final Logger logger = LoggerFactory.getLogger(DeployInfo.class);


    public DeployInfo() {
        service= new ExternalResource();
        template = new ReferenceObject();
        configurations = new HashMap<>();
    }

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating appComponent [" + getId() + "]");
    }
}
