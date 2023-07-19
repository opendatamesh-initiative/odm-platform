package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Schema")
@Table(name = "DEF_SCHEMAS", schema="ODMREGISTRY")
public class Schema {
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "MEDIA_TYPE")
    private String mediaType; 

    @Column(name = "CONTENT")
    private String content;  
}
