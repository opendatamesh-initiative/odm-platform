package org.opendatamesh.platform.pp.api.database.entities.dataproduct;



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
   
    @Column(name = "FQN")
    private String fullyQualifiedName;
    
    @Column(name = "ENTITY_TYPE")
    private String entityType;
   
    @Column(name = "NAME")
    private String name;
   
    @Column(name = "DISPLAY_NAME")
    private String displayName;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "DOMAIN")
    private String domain;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
    private Owner owner;
    
    
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPDS_INFO_CONTACT_POINTS", joinColumns = {@JoinColumn(name = "ID"), @JoinColumn(name = "VERSION")})
    @Column(name = "CONTACT_POINT_ID") 
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ContactPoint> contactPoints = new ArrayList<ContactPoint>();


    public Info() { }
   
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
