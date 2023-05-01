package org.opendatamesh.platform.pp.api.database.entities.dataproduct;

import javax.persistence.*;

import org.opendatamesh.platform.pp.api.utils.HashMapConverter;

import lombok.Data;

import java.util.Map;

@Data
@Entity( name = "BuildInfo" )
@Table( name = "DPDS_APP_COMPONENT_BUILD_INFOS", schema="PUBLIC")
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "ID")
    private ExternalResource template;

    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;

    public BuildInfo() {
    }

    public BuildInfo(ExternalResource service, ExternalResource template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }

    @Override
    public String toString() {
        return "BuildInfo{" +
                "id=" + id +
                ", service=" + service +
                ", template=" + template +
                ", configurations=" + configurations +
                '}';
    }
}
