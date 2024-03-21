package org.opendatamesh.platform.pp.policy.api.resources.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityStageTransitionEventTypeResource {

    @JsonProperty("lifecycle")
    LifecycleResource lifecycle;

    @JsonProperty("activity")
    ActivityResource activity;

    @JsonProperty("tasks")
    List<TaskResource> tasks;

    public LifecycleResource getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(LifecycleResource lifecycle) {
        this.lifecycle = lifecycle;
    }

    public ActivityResource getActivity() {
        return activity;
    }

    public void setActivity(ActivityResource activity) {
        this.activity = activity;
    }

    public List<TaskResource> getTasks() {
        return tasks;
    }

    public void setTask(List<TaskResource> tasks) {
        this.tasks = tasks;
    }

}
