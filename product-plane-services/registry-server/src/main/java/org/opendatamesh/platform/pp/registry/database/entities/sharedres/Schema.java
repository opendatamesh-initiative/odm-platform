package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "Schema")
@Table(name = "DPDS_SCHEMAS", schema="PUBLIC")
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
