package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataProductVersionId implements Serializable {
    private String dataProductId;
    private String versionNumber;
}
