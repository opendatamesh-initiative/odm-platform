package org.opendatamesh.platform.pp.policy.server.services.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.server.services.proxies.DevOpsProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyEnricherService {

    @Autowired
    DevOpsProxy devOpsProxy;

    private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    private static final Logger logger = LoggerFactory.getLogger(PolicyEnricherService.class);

    public PolicyEnricherService() { }

    public PolicyEvaluationRequestResource enrichRequest(PolicyEvaluationRequestResource request) {
        switch (request.getEvent()) {
            case TASK_EXECUTION_RESULT:
                return enrichTaskExecutionResultEvent(request);
            default:
                return request;
        }
    }

    private PolicyEvaluationRequestResource enrichTaskExecutionResultEvent(PolicyEvaluationRequestResource request) {
        try {
            TaskResultEventTypeResource taskResultEventTypeResource = mapper.readValue(
                    request.getCurrentState().asText(), TaskResultEventTypeResource.class
            );
            if(taskResultEventTypeResource != null) {
                ActivityResource activityResource = devOpsProxy.getActivityById(
                        taskResultEventTypeResource.getTask().getActivityId()
                );
                taskResultEventTypeResource.setActivity(activityResource);
                request.setCurrentState(JsonNodeUtils.toJsonNode(taskResultEventTypeResource));
                request.setDataProductId(activityResource.getDataProductId());
                request.setDataProductVersion(activityResource.getDataProductVersion());
            }
        } catch (Exception e) {
            logger.warn("Error enriching TASK_EXECUTION_RESULT event", e);
        }
        return request;
    }

}
