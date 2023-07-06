package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "ExternalResource")
@Table(name = "DPV_EXTERNAL_RESOURCES", schema="PUBLIC")
public class ExternalResource {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MEDIA_TYPE")
    private String mediaType;

    @Column(name = "HREF")
    private String href;

    public ExternalResource() {
    }

    public ExternalResource(String description, String mediaType, String href) {
        this.description = description;
        this.mediaType = mediaType;
        this.href = href;
    }
}
