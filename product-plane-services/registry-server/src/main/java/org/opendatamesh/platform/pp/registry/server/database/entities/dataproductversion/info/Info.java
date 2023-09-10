package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info;


import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Embeddable
public class Info implements Serializable, Cloneable{

    @Transient
    private String dataProductId;

    @Transient
    private String versionNumber;
   
    @Column(name = "DP_FQN")
    private String fullyQualifiedName;
    
    @Column(name = "DP_ENTITY_TYPE")
    private String entityType;
   
    @Column(name = "DP_NAME")
    private String name;
   
    @Column(name = "DP_DISPLAY_NAME")
    private String displayName;
    
    @Column(name = "DP_DOMAIN")
    private String domain;

    @Column(name = "DESCRIPTION")
    private String description;
    
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
    private Owner owner;
    
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_INFO_CONTACT_POINTS", schema="ODMREGISTRY", joinColumns = {@JoinColumn(name = "DATA_PRODUCT_ID"), @JoinColumn(name = "VERSION_NUMBER")})
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ContactPoint> contactPoints = new ArrayList<ContactPoint>();   
}
