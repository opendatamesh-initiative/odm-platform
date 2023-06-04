package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

import javax.persistence.*;

import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;

import java.util.Map;

@Data
@Entity(name = "ProvisionInfo")
@Table(name = "DPDS_INFRA_PROVISION_INFOS", schema="PUBLIC")
public class ProvisionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne(mappedBy = "provisionInfo")
    InfrastructuralComponent infraComponent;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
    private ExternalResource service;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "ID")
    private ExternalResource template;

    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;

    public ProvisionInfo() {
    }

    public ProvisionInfo(ExternalResource service, ExternalResource template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }


    @Override
    public String toString() {
        return "ProvisionInfo{" +
                "id=" + id +
                ", service=" + service +
                ", template=" + template +
                ", configurations=" + configurations +
                '}';
    }
}
