package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.InternalComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@Entity(name = "ApplicationComponent")
@Table(name = "DPV_APP_COMPONENTS", schema="ODMREGISTRY")
public class ApplicationComponent extends InternalComponent implements Cloneable {

    @Column(name = "PLATFORM")
    private String platform;

    @Column(name = "APPLICATION_TYPE")
    private String applicationType;
                                       
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "DPV_APP_COMPONENT_SOURCES", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "SOURCE_ID") 
    private List<String> consumesFrom = new ArrayList<String>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "DPV_APP_COMPONENT_SINKS", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "SINK_ID") 
    private List<String> providesTo = new ArrayList<String>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_APP_COMPONENT_DEPENDENCIES", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "COMPONENT_ID"))
    @Column(name = "DEPENDS_ON_COMPONENT_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> dependsOn = new ArrayList<String>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_APP_COMPONENT_TAGS", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;

    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationComponent.class);

   
    @PrePersist
    protected void onCreate() {
        logger.debug("Creating appComponent [" + getId() + "]");
    }
}
