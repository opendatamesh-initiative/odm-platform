package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Embeddable
public class InterfaceComponents {

    @Transient
    private static Environment environment;

    @PersistenceContext
    private transient EntityManager entityManager;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<Port> ports = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    @Where(clause = "\"ENTITY_TYPE\" = 'inputport'")
    private List<Port> inputPorts = new ArrayList<Port>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    @Where(clause = "\"ENTITY_TYPE\" = 'outputport'")
    private List<Port> outputPorts = new ArrayList<Port>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    @Where(clause = "\"ENTITY_TYPE\" = 'discoveryport'")
    private List<Port> discoveryPorts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    @Where(clause = "\"ENTITY_TYPE\" = 'observabilityport'")
    private List<Port> observabilityPorts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns( {
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    @Where(clause = "\"ENTITY_TYPE\" = 'controlport'")
    private List<Port> controlPorts = new ArrayList<>();

    @PostLoad
    private void portAssigner() throws SQLException {
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
