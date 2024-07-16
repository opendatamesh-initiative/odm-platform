package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info.Info;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces.InterfaceComponents;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.InternalComponents;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity(name = "DataProductVersion")
@Table(name = "DP_VERSIONS", schema="ODMREGISTRY")
@IdClass(DataProductVersionId.class)
public class DataProductVersion implements Cloneable, Serializable {

    @Id
    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Id
    @Column(name = "VERSION_NUMBER")
    private String versionNumber;

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
    @CollectionTable(name = "DPV_DATA_PRODUCT_TAGS", schema="ODMREGISTRY", joinColumns = {@JoinColumn(name = "DATAPRODUCT_ID"), @JoinColumn(name = "VERSION")})
    @Column(name = "TAG_ID")
    @Fetch(value = FetchMode.SUBSELECT)
    protected List<String> tags = new ArrayList<String>();


    @Column(name = "DPDS_VERSION")
    private String dataProductDescriptor;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    public boolean hasInternalComponents() {
        return getInternalComponents() != null;
    }

    public boolean hasApplicationComponents() {
        return hasInternalComponents() && getInternalComponents().hasApplicationComponents();
    }

    public boolean hasInfrastructuralComponents() {
        return hasInternalComponents() && getInternalComponents().hasInfrastructuralComponents();
    }

    public boolean hasLifecycleInfo() {
        return hasInternalComponents() && getInternalComponents().hasLifecycleInfo();
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
}