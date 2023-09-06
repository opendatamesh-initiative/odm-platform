package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces;


import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.Component;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.InternalComponent;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "Port")
@Table(name = "DPV_PORTS", schema="ODMREGISTRY")
public class Port extends InternalComponent implements Cloneable{

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROMISES_ID", referencedColumnName = "ID")
    private Promises promises;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXPECTATIONS_ID", referencedColumnName = "ID")
    private Expectations expectations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CONTRACTS_ID", referencedColumnName = "ID")
    private Contracts contracts;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_PORT_TAGS", schema="ODMREGISTRY", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;

    public boolean hasApi() {
        return getPromises() != null && getPromises().getApi() != null;
    }

    public boolean hasApiDefinition() {
        return hasApi() && getPromises().getApi().hasDefinition();
    }
}
