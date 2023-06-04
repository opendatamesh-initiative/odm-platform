package org.opendatamesh.platform.pp.registry.database.entities.dataproduct;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataProductVersionId implements Serializable {
    private String dataProductId;
    private String versionNumber;
}
