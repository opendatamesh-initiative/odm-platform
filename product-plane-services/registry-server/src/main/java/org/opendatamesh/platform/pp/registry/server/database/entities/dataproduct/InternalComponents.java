package org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Embeddable
public class InternalComponents {

    @Embedded
    private LifecycleInfo lifecycleInfo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns({
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<ApplicationComponent> applicationComponents = new ArrayList<ApplicationComponent>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns({
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<InfrastructuralComponent> infrastructuralComponents = new ArrayList<InfrastructuralComponent>();

    // private static final Logger logger =
    // LoggerFactory.getLogger(InternalComponents.class);

    public boolean hasApplicationComponents() {
        return applicationComponents != null && !applicationComponents.isEmpty();
    }

    public boolean hasInfrastructuralComponents() {
        return infrastructuralComponents != null && !infrastructuralComponents.isEmpty();
    }

    public boolean hasLifecycleInfo() {
        return lifecycleInfo != null 
            && lifecycleInfo.getActivityInfos() != null 
            && !lifecycleInfo.getActivityInfos().isEmpty();
    }

}