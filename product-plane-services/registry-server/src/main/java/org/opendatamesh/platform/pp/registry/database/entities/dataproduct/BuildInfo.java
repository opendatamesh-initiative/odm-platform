package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity( name = "BuildInfo" )
@Table( name = "DPV_APP_COMPONENT_BUILD_INFOS", schema="PUBLIC")
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
    private ReferenceObject template;

    @Column(name = "CONFIGURATIONS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> configurations;
}
