package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
@Embeddable
public class InternalComponents {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    //@JoinColumn(name = "DATA_PRODUCT_ID")
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "VERSION")
    })
    private List<ApplicationComponent> applicationComponents = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    //@JoinColumn(name = "DATA_PRODUCT_ID")
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "VERSION")
    })
    private List<InfrastructuralComponent> infrastructuralComponents = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(InternalComponents.class);
    
    public void replaceInfrastructuralComponent(InfrastructuralComponent oldDefinition, InfrastructuralComponent newDefinition){
        this.infrastructuralComponents.remove(oldDefinition);
        this.infrastructuralComponents.add(newDefinition);
    }

    public void replaceApplicationComponent(ApplicationComponent oldDefinition, ApplicationComponent newDefinition){
        this.applicationComponents.remove(oldDefinition);
        this.applicationComponents.add(newDefinition);
    }

    @PrePersist
    protected void onCreate() {
        logger.debug("Creating InternalComponents []");
    }
}
