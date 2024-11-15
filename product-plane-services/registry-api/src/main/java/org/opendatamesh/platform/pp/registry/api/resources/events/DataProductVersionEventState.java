package org.opendatamesh.platform.pp.registry.api.resources.events;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;

public class DataProductVersionEventState {
    private DataProductVersionDPDS dataProductVersion;

    public DataProductVersionEventState() {
    }

    public DataProductVersionEventState(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
