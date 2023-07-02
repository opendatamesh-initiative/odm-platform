package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "Port")
@Table(name = "DPV_PORTS", schema="PUBLIC")
public class Port extends Component implements Cloneable{

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
    @CollectionTable(name = "DPV_PORT_TAGS", schema="PUBLIC", joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;
}
