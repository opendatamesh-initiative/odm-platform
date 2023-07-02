package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;


@Data
@Entity(name = "ReferenceObject")  // cambiare nome
@Table(name = "DPV_REFERENCE_OBJECTS", schema="PUBLIC")
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
