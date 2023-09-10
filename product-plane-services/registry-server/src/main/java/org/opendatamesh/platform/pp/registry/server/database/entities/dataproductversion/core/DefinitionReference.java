package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Data;

@Embeddable
@Data
public class DefinitionReference  {
    @Column(name = "DEF_DESCRIPTION")
    private String description;

    @Column(name = "DEF_MEDIA_TYPE")
    private String mediaType;

    @Column(name = "DEF_REF")
    private String ref;

    @Column(name = "DEF_ORIGINAL_REF")
    private String originalRef;

    @Transient
    private String rawContent;
}
