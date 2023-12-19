package org.opendatamesh.platform.pp.devops.server.resources.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Context {

    @JsonProperty("context")
    private Map<String, ActivityContext> context;

    public Context() {
        this.context = new HashMap<>();
    }

    public void concatenateActivitiesContext(String activityStageName, ActivityContext activityContext) {
        context.put(activityStageName, activityContext);
    }

}
