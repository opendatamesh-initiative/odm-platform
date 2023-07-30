package org.opendatamesh.platform.core.dpds.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LifecycleInfoDPDS {
    private List<LifecycleActivityInfoDPDS> activityInfos;

    public LifecycleInfoDPDS() {
        activityInfos = new ArrayList<LifecycleActivityInfoDPDS>();
    }   

    @JsonIgnore
    public LifecycleActivityInfoDPDS getActivityInfo(String stageName) {
        Objects.requireNonNull(stageName, "Parameter stageName cannot be null");
        for(LifecycleActivityInfoDPDS activity: activityInfos) {
            if(stageName.equals(activity.getStageName())) {
                return activity;
            }
        }
        return null;
    }

}
