package org.opendatamesh.platform.pp.policy.server.services.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
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

    public PolicyEnricherService() {
    }

    public void enrichRequest(PolicyEvaluationRequestResource request) {
        if (PolicyEvaluationRequestResource.EventType.TASK_EXECUTION_RESULT.equals(request.getEvent())) {
            enrichTaskExecutionResultEvent(request);
        }
    }

    private void enrichTaskExecutionResultEvent(PolicyEvaluationRequestResource request) {
        try {
            TaskResultEventTypeResource taskResultEventTypeResource = mapper.treeToValue(
                    request.getCurrentState(), TaskResultEventTypeResource.class
            );
            if (taskResultEventTypeResource != null) {
                ActivityResource activityResource = devOpsProxy.getActivityById(
                        taskResultEventTypeResource.getTask().getActivityId()
                );
                taskResultEventTypeResource.setActivity(activityResource);
                request.setCurrentState(JsonNodeUtils.toJsonNode(taskResultEventTypeResource));
                if (activityResource != null) {
                    request.setDataProductId(activityResource.getDataProductId());
                    request.setDataProductVersion(activityResource.getDataProductVersion());
                }
            }
        } catch (Exception e) {
            logger.warn("Error enriching TASK_EXECUTION_RESULT event", e);
        }
    }

}
