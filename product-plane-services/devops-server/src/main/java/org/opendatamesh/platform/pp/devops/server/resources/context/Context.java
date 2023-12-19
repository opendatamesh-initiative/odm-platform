package org.opendatamesh.platform.pp.devops.server.resources.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

import java.util.HashMap;
import java.util.Map;

@Data
public class Context {

    @JsonProperty("context")
    private Map<String, ActivityContext> context;

    public Context() {
        this.context = new HashMap<>();
    }

    public ObjectNode toObjectNode() throws JsonProcessingException {
        return ObjectMapperFactory.JSON_MAPPER.readValue(
                ObjectMapperFactory.JSON_MAPPER.writeValueAsString(this.context),
                ObjectNode.class
        );
    }

    public void concatenateActivitiesContext(String activityStageName, ActivityContext activityContext) {
        context.put(activityStageName, activityContext);
    }

}
