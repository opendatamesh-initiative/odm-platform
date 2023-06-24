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
@Table(name = "DPDS_REFERENCE_OBJECTS", schema="PUBLIC")
public class ReferenceObject {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "DESCRIPTION")
    private String description;

    @Transient
    private String mediaType;

    // Eliminare ?
    @Column(name = "REF")
    private String ref;

    // ELiminare
    @Column(name = "ORIGINAL_REF")
    private String originalRef;

    @Column(name="CONTENT", columnDefinition = "LONGTEXT")
    private String rawContent;
   
    public ReferenceObject() { }

    public ReferenceObject(String ref, String description) {
        this.ref = ref;
        this.description = description;
    }
}
