package org.opendatamesh.platform.pp.api.database.entities.dataproduct;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "ExternalResource")
@Table(name = "DPDS_EXTERNAL_RESOURCES", schema="PUBLIC")
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
