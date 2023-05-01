package org.opendatamesh.platform.pp.api.database.entities.dataproduct;

import javax.persistence.*;

import lombok.Data;

import java.util.List;

@Data
@Entity(name = "Owner")
@Table(name = "DPDS_INFO_OWNERS", schema="PUBLIC")
public class Owner {

    @Id
    @Column(name = "ID")
    String id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy="info.owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<DataProductVersion> dataproduct;

    @Override
    public String toString() {
        return "Owner{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
