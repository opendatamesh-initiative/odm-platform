package org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity(name = "DataProduct")
@Table(name = "DATA_PRODUCTS", schema="ODMREGISTRY")
public class DataProduct {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FQN")
    private String fullyQualifiedName;
   
    @Column(name = "DOMAIN")
    private String domain;

    @Column(name = "DESCRIPTION")
    private String description;
}
