package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity(name = "ReferenceObject")  // cambiare nome
@Table(name = "DPV_REFERENCE_OBJECTS", schema="ODMREGISTRY")
public class ReferenceObject {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MEDIA_TYPE")
    private String mediaType;

    @Column(name = "REF")
    private String ref;

    @Column(name = "ORIGINAL_REF")
    private String originalRef;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;
   
}
