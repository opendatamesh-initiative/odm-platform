package org.opendatamesh.platform.pp.policy.server.services.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.services.mappers.JsonNodeMapper;
import org.opendatamesh.platform.pp.policy.server.config.OdmClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyEnricherService {

    @Autowired
    OdmClients odmClients;

    private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    private static final Logger logger = LoggerFactory.getLogger(PolicyEnricherService.class);

    public PolicyEnricherService() { }

    public PolicyEvaluationRequestResource enrichRequest(PolicyEvaluationRequestResource request) {
        switch (request.getEvent()) {
            case ACTIVITY_EXECUTION_RESULT:
                return enrichActivityExecutionResultEvent(request);
            default:
                return request;
        }
    }

    private PolicyEvaluationRequestResource enrichActivityExecutionResultEvent(PolicyEvaluationRequestResource request) {
        if(odmClients.getDevOpsClient() != null) {
            try {
                TaskResultEventTypeResource taskResultEventTypeResource = mapper.readValue(
                        request.getCurrentState().asText(), TaskResultEventTypeResource.class
                );
                if(taskResultEventTypeResource != null) {
                    ActivityResource activityResource = odmClients.getDevOpsClient().readActivity(
                            Long.valueOf(taskResultEventTypeResource.getTask().getActivityId())
                    );
                    taskResultEventTypeResource.setActivity(activityResource);
                    request.setCurrentState(JsonNodeMapper.toJsonNode(taskResultEventTypeResource));
                }
            } catch (Throwable t) {
                logger.warn("Error enriching ACTIVITY_EXECUTION_RESULT event", t);
            }
        }
        return request;
    }

}
