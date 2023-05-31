package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.UUID;

import javax.persistence.*;


@Data
@Entity(name = "DataProduct")
@Table(name = "DPDS_DATA_PRODUCTS", schema="PUBLIC")
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

    /* 
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "dataProduct")
    private List<DataProductVersion> dataProductVersions = new ArrayList<>();
    */

    public DataProduct() {
    }
    
    public DataProduct(String id) {
        this.id = id;
    }

    public DataProduct(DataProductVersion dataProductVersion) {
        if(dataProductVersion == null) {
            throw new RuntimeException("Input data product version cannot be null");
        }

        if(dataProductVersion.getInfo().getFullyQualifiedName() == null) {
            throw new RuntimeException("The fully qualified name of product is not specified in the product version");
        } else {
            this.fullyQualifiedName = dataProductVersion.getInfo().getFullyQualifiedName();
        }
        this.id =  UUID.nameUUIDFromBytes(fullyQualifiedName.getBytes()).toString();
        this.domain = dataProductVersion.getInfo().getDomain();
    }

    public DataProduct(String id, String fullyQualifiedName, String domain) {
        this.id = id;
        this.fullyQualifiedName = fullyQualifiedName;
        this.domain = domain;
    }

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
