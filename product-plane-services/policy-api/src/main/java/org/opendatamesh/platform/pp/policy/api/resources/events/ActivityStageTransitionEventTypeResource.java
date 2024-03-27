package org.opendatamesh.platform.pp.policy.api.resources.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

import java.util.ArrayList;
import java.util.List;

public class ActivityStageTransitionEventTypeResource {

    @JsonProperty("lifecycle")
    LifecycleResource lifecycle = null;

    @JsonProperty("activity")
    ActivityResource activity = null;

    @JsonProperty("tasks")
    List<TaskResource> tasks = null;

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
