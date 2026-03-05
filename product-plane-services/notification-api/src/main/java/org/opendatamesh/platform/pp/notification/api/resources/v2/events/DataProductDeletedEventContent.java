package org.opendatamesh.platform.pp.notification.api.resources.v2.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Expected shape of {@code EventV2Resource.eventContent} for DATA_PRODUCT_DELETED V2 events.
 * Mirrors the registry's emitted payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductDeletedEventContent {

    @Schema(description = "UUID of the deleted data product", example = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0")
    private String dataProductUuid;

    @Schema(description = "Fully qualified name of the data product", example = "domain:productName")
    private String dataProductFqn;

    public String getDataProductUuid() {
        return dataProductUuid;
    }

    public void setDataProductUuid(String dataProductUuid) {
        this.dataProductUuid = dataProductUuid;
    }

    public String getDataProductFqn() {
        return dataProductFqn;
    }

    public void setDataProductFqn(String dataProductFqn) {
        this.dataProductFqn = dataProductFqn;
    }
}
