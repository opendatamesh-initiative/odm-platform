package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class ContactPoint {
    
    @Column(name = "NAME")
    private String name;
   
    @Column(name = "DESCRIPTION")
    private String description;
   
    @Column(name = "CHANNEL")
    private String channel;
    
    @Column(name = "ADDRESS")
    private String address;
}
