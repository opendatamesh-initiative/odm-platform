package org.opendatamesh.platform.pp.policy.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.services.mappers.JsonNodeMapper;
import org.springframework.stereotype.Service;

@Service
public class PolicyEnricher {

    DevOpsClient devOpsClient;

    public PolicyEnricher() {
        this.devOpsClient = new DevOpsClient("fakeAddress");
    }

    public JsonNode enrichInputObject(PolicyEvaluationRequestResource.EventType eventType, JsonNode inputObject) {

        return inputObject;
    };

    public PolicyEvaluationRequestResource enrichRequest(PolicyEvaluationRequestResource request) {
        if(request.getEvent().equals(PolicyEvaluationRequestResource.EventType.TASK_EXECUTION_RESULT)) {
            ActivityResource activityResource = devOpsClient.readActivity(
                    request.getCurrentState().get("task").get("activityId").asLong()
            );
            ObjectNode currentStateNode = (ObjectNode) request.getCurrentState();
            currentStateNode.put("activity", JsonNodeMapper.toJsonNode(activityResource));
            request.setCurrentState(currentStateNode);
        }
        return request;
    }

}
