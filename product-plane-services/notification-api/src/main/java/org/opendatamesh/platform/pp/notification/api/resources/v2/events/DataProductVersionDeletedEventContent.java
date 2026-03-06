package org.opendatamesh.platform.pp.notification.api.resources.v2.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Expected shape of {@code EventV2Resource.eventContent} for DATA_PRODUCT_VERSION_DELETED V2 events.
 * Mirrors the registry's emitted payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductVersionDeletedEventContent {

    @Schema(description = "UUID of the deleted data product version", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String dataProductVersionUuid;

    @Schema(description = "Fully qualified name of the data product", example = "domain:productName")
    private String dataProductFqn;

    @Schema(description = "Version number of the deleted data product version", example = "1.0.0")
    private String dataProductVersionNumber;

    @Schema(description = "Version tag (e.g. semantic version)", example = "1.0.0")
    private String dataProductVersionTag;

    public String getDataProductVersionUuid() {
        return dataProductVersionUuid;
    }

    public void setDataProductVersionUuid(String dataProductVersionUuid) {
        this.dataProductVersionUuid = dataProductVersionUuid;
    }

    public String getDataProductFqn() {
        return dataProductFqn;
    }

    public void setDataProductFqn(String dataProductFqn) {
        this.dataProductFqn = dataProductFqn;
    }

    public String getDataProductVersionNumber() {
        return dataProductVersionNumber;
    }

    public void setDataProductVersionNumber(String dataProductVersionNumber) {
        this.dataProductVersionNumber = dataProductVersionNumber;
    }

    public String getDataProductVersionTag() {
        return dataProductVersionTag;
    }

    public void setDataProductVersionTag(String dataProductVersionTag) {
        this.dataProductVersionTag = dataProductVersionTag;
    }
}
