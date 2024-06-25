package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces;

import lombok.Data;
import org.opendatamesh.dpds.model.core.EntityTypeDPDS;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Embeddable
public class InterfaceComponents {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> ports = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> inputPorts = new ArrayList<Port>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> outputPorts = new ArrayList<Port>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> discoveryPorts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> observabilityPorts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> controlPorts = new ArrayList<>();

    @PostLoad
    private void portAssigner() {
        this.inputPorts = ports.stream().filter(e -> e.getEntityType().equals("inputport")).collect(Collectors.toList());
        this.outputPorts = ports.stream().filter(e -> e.getEntityType().equals("outputport")).collect(Collectors.toList());
        this.discoveryPorts = ports.stream().filter(e -> e.getEntityType().equals("discoveryport")).collect(Collectors.toList());
        this.observabilityPorts = ports.stream().filter(e -> e.getEntityType().equals("observabilityport")).collect(Collectors.toList());
        this.controlPorts = ports.stream().filter(e -> e.getEntityType().equals("controlport")).collect(Collectors.toList());
    }

    public List<Port> getPortListByEntityType(EntityTypeDPDS entityType){
        switch (entityType){
            case OUTPUTPORT:
                return outputPorts;
            case INPUTPORT:
                return inputPorts;
            case CONTROLPORT:
                return controlPorts;
            case DISCOVERYPORT:
                return discoveryPorts;
            case OBSERVABILITYPORT:
                return observabilityPorts;
            default:
                return null;
        }
    }

}
