package org.opendatamesh.platform.pp.policy.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.services.mappers.JsonNodeMapper;
import org.springframework.stereotype.Service;

@Service
public class PolicyEnricher {

    DevOpsClient devOpsClient;

    private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    public PolicyEnricher() {
        this.devOpsClient = new DevOpsClient("fakeAddress");
    }

    public JsonNode enrichInputObject(PolicyEvaluationRequestResource.EventType eventType, JsonNode inputObject) {

        return inputObject;
    };

    public PolicyEvaluationRequestResource enrichRequest(PolicyEvaluationRequestResource request) {
        if(request.getEvent().equals(PolicyEvaluationRequestResource.EventType.TASK_EXECUTION_RESULT)) {

            TaskResultEventTypeResource taskResultEventTypeResource = null;
            try {
                taskResultEventTypeResource = mapper.readValue(
                        request.getCurrentState().asText(), TaskResultEventTypeResource.class
                );
                if(taskResultEventTypeResource != null) {
                    ActivityResource activityResource = devOpsClient.readActivity(
                            Long.valueOf(taskResultEventTypeResource.getTask().getActivityId())
                    );
                    taskResultEventTypeResource.setActivity(activityResource);
                    request.setCurrentState(JsonNodeMapper.toJsonNode(taskResultEventTypeResource));
                }
            } catch (Exception e) {
                // nothing
            }
        }
        return request;
    }

}
