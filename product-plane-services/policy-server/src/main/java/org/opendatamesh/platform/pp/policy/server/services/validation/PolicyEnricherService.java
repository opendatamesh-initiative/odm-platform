package org.opendatamesh.platform.pp.policy.server.services.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.services.mappers.JsonNodeMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PolicyEnricherService {

    @Value("${odm.productPlane.devopsService.active}")
    private Boolean devOpsServiceActive;

    @Value("${odm.productPlane.devopsService.address}")
    private String devOpsServerAddress;

    DevOpsClient devOpsClient;

    private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    public PolicyEnricherService() {

        if(devOpsServiceActive)
            this.devOpsClient = new DevOpsClient("fakeAddress");
        else
            this.devOpsClient = null;
    }

    public JsonNode enrichInputObject(PolicyEvaluationRequestResource.EventType eventType, JsonNode inputObject) {

        return inputObject;
    };

    public PolicyEvaluationRequestResource enrichRequest(PolicyEvaluationRequestResource request) {
        switch (request.getEvent()) {
            case ACTIVITY_EXECUTION_RESULT:
                return enrichActivityExecutionResultEvent(request);
            default:
                return request;
        }
    }

    private PolicyEvaluationRequestResource enrichActivityExecutionResultEvent(PolicyEvaluationRequestResource request) {
        if(devOpsServiceActive) {
            try {
                TaskResultEventTypeResource taskResultEventTypeResource = mapper.readValue(
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
