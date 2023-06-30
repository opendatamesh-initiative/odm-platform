package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Template")
@Table(name = "DPDS_TEMPLATE", schema="PUBLIC")
public class Template {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MEDIA_TYPE")
    private String mediaType;

    @Column(name = "REF")
    private String ref;

    public Template() {
    }

    public Template(String description, String mediaType, String ref) {
        this.description = description;
        this.mediaType = mediaType;
        this.ref = ref;
    }

}
