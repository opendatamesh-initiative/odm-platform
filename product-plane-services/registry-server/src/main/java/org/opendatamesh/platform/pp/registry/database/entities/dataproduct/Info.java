package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;



import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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
    @CollectionTable(name = "DPV_INFO_CONTACT_POINTS", joinColumns = {@JoinColumn(name = "DATA_PRODUCT_ID"), @JoinColumn(name = "VERSION_NUMBER")})
    //@Column(name = "CONTACT_POINT_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ContactPoint> contactPoints = new ArrayList<ContactPoint>();   
}
