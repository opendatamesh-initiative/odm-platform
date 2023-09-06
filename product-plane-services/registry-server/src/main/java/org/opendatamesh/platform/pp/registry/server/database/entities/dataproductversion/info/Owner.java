package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info;

import lombok.Data;

import javax.persistence.*;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;

import java.util.List;

@Data
@Entity(name = "Owner")
@Table(name = "DPV_INFO_OWNERS", schema="ODMREGISTRY")
public class Owner {

    @Id
    @Column(name = "ID")
    String id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy="info.owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<DataProductVersion> dataproduct;
}
