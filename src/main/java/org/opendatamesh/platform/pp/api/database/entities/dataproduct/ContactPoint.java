package org.opendatamesh.platform.pp.api.database.entities.dataproduct;



import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

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
