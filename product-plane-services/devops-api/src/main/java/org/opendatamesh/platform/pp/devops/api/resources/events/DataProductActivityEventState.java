package org.opendatamesh.platform.pp.devops.api.resources.events;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;

public class DataProductActivityEventState {
    private ActivityResource activity;
    private DataProductVersionDPDS dataProductVersion;

    public DataProductActivityEventState() {
    }

    public DataProductActivityEventState(ActivityResource activity, DataProductVersionDPDS dataProductVersion) {
        this.activity = activity;
        this.dataProductVersion = dataProductVersion;
    }

    public ActivityResource getActivity() {
        return activity;
    }

    public void setActivity(ActivityResource activity) {
        this.activity = activity;
    }

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
