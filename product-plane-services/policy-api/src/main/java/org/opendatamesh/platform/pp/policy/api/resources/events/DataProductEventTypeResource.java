package org.opendatamesh.platform.pp.policy.api.resources.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductEventTypeResource {
    @JsonProperty("dataProductVersion")
    DataProductVersionDPDS dataProductVersion;

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

}
