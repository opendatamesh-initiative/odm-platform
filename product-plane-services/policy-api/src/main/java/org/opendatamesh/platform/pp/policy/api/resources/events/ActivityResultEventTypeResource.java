package org.opendatamesh.platform.pp.policy.api.resources.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;

public class ActivityResultEventTypeResource {

    @JsonProperty("activity")
    ActivityResource activity = null;

    @JsonProperty("dataProductVersion")
    DataProductVersionDPDS dataProductVersion = null;

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
