package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;


@Data
@Entity(name = "DataProductVersion")
@Table(name = "DPDS_DATA_PRODUCT_VERSIONS")
@IdClass(DataProductVersionId.class)
public class DataProductVersion implements Cloneable, Serializable {

    @Id
    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Id
    @Column(name = "VERSION_NUMBER")
    private String versionNumber;

    @Column(name = "DPDS_VERSION")
    private String dataProductDescriptor;

    @Embedded
    private Info info;

    @Embedded
    private InterfaceComponents interfaceComponents;

    @Embedded
    private InternalComponents internalComponents;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPDS_DATA_PRODUCT_TAGS", schema="PUBLIC", joinColumns = {@JoinColumn(name = "DATAPRODUCT_ID"), @JoinColumn(name = "VERSION")})
    @Column(name = "TAG_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    protected List<String> tags = new ArrayList<String>();

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;

    /**
     * 
     * @return the reffered data product
     */
    public DataProduct getDataProduct() {
        return new DataProduct(this); 
    }

    /**
     * 
     * @param dataProduct a data product
     * @param dataProductVersion the data product version
     * @return true if the referred product match with the one passed as input, false otherwise
     */
    private boolean isVersionOf(DataProduct dataProduct) {
        DataProduct referredDataProduct = this.getDataProduct();
        return dataProduct.equals(referredDataProduct);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        getInfo().setDataProductId(dataProductId);
        getInfo().setVersionNumber(versionNumber);
    }

    @PostLoad
    protected void onRead() {
        getInfo().setDataProductId(dataProductId);
        getInfo().setVersionNumber(versionNumber);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public DataProductVersion clone() throws CloneNotSupportedException {
        return (DataProductVersion) super.clone();
    }

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

}